import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import type { BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query/react'

export interface Credentials {
  username: string
  password: string
}

export interface CurrentUser {
  id: number
  username: string
}

export const positions = ['DIRECTOR', 'HEAD_OF_DIVISION', 'LEAD_DEVELOPER']
export const statuses = ['HIRED', 'RECOMMENDED_FOR_PROMOTION', 'REGULAR']
export const organizationTypes = [
  'PUBLIC',
  'GOVERNMENT',
  'TRUST',
  'PRIVATE_LIMITED_COMPANY',
  'OPEN_JOINT_STOCK_COMPANY',
]
export const colors = ['BLACK', 'BLUE', 'ORANGE', 'WHITE', 'BROWN']
export const countries = ['RUSSIA', 'UNITED_KINGDOM', 'SPAIN', 'ITALY']

export interface Coordinates {
  id: number
  x: number
  y: number
}

export interface Location {
  id: number
  x: number
  y: number
  z: number
}

export interface Address {
  id: number
  street: string
  town: Location | null
}

export interface Organization {
  id: number
  full_name: string
  annual_turnover: number
  employees_count: number
  rating: number
  type: string | null
  official_address: Address
  postal_address: Address
}

export interface Person {
  id: number
  eye_color: string | null
  hair_color: string
  location: Location
  birthday: string
  weight: number
  nationality: string
}

export interface Worker {
  id: number
  creator_id: number
  name: string
  coordinates: Coordinates
  creation_date: string
  organization: Organization | null
  salary: number
  rating: number
  start_date: string
  position: string | null
  status: string | null
  person: Person | null
}

export interface Page<T> {
  items: T[]
  page: number
  page_size: number
  total_items: number
}

export interface WorkerListParams {
  page: number
  page_size: number
  sort_by: string
  sort_order: string
  name?: string
  position?: string
  status?: string
}

export interface IdRef {
  id: number
}

export interface LocationFields {
  x: number
  y: number
  z: number
}

export interface AddressRequest {
  street: string
  town: LocationFields | IdRef | null
}

export interface OrganizationRequest {
  full_name: string
  annual_turnover: number
  employees_count: number
  rating: number
  type: string | null
  official_address?: AddressRequest
  official_address_id?: number
  postal_address?: AddressRequest
  postal_address_id?: number
}

export interface PersonRequest {
  eye_color: string | null
  hair_color: string
  location: LocationFields | IdRef
  birthday: string
  weight: number
  nationality: string
}

export interface WorkerRequest {
  name: string
  coordinates: { x: number; y: number } | IdRef
  salary: number
  rating: number
  start_date: string
  position: string | null
  status: string | null
  person: PersonRequest | IdRef | null
  organization_id?: number | null
  organization?: OrganizationRequest
}

interface CsrfTokenResponse {
  csrf_token: string
}

interface IdResponse {
  id: number
}

interface CreateWorkerResponse {
  id: number
  organization_id: number | null
}

// Списки для выбора существующих объектов: сервер отдаёт не больше 100 за раз.
const choiceParams = { page: 1, page_size: 100 }

const dataTags = ['Worker', 'Coordinates', 'Location', 'Address', 'Organization', 'Person'] as const

const rawBaseQuery = fetchBaseQuery({ baseUrl: '/api/v1', credentials: 'include' })

// Токен текущей сессии хранится только в памяти. Сервер меняет его после входа и выхода,
// поэтому тогда он сбрасывается и перед следующим изменяющим запросом запрашивается заново.
let csrfToken: string | null = null

const baseQuery: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (
  args,
  queryApi,
  extraOptions,
) => {
  const request = typeof args === 'string' ? { url: args } : args
  if (!request.method || request.method === 'GET') {
    const result = await rawBaseQuery(request, queryApi, extraOptions)
    // Сессия могла закончиться: перечитываем её, чтобы приложение вернулось к экрану входа.
    if (result.error?.status === 401 && request.url !== '/auth/session') {
      queryApi.dispatch(api.util.invalidateTags(['Session']))
    }
    return result
  }

  if (!csrfToken) {
    const csrf = await rawBaseQuery('/auth/csrf', queryApi, extraOptions)
    if (csrf.error) {
      return csrf
    }
    csrfToken = (csrf.data as CsrfTokenResponse).csrf_token
  }

  const result = await rawBaseQuery(
    { ...request, headers: { ...request.headers, 'X-CSRF-Token': csrfToken } },
    queryApi,
    extraOptions,
  )
  if (result.error?.status === 403 || request.url === '/auth/login' || request.url === '/auth/logout') {
    csrfToken = null
  }
  return result
}

export const api = createApi({
  baseQuery,
  tagTypes: ['Session', ...dataTags],
  endpoints: (build) => ({
    getSession: build.query<CurrentUser, void>({
      query: () => '/auth/session',
      providesTags: ['Session'],
    }),
    register: build.mutation<IdResponse, Credentials>({
      query: (body) => ({ url: '/auth/register', method: 'POST', body }),
    }),
    login: build.mutation<CurrentUser, Credentials>({
      query: (body) => ({ url: '/auth/login', method: 'POST', body }),
      invalidatesTags: (_result, error) => (error ? [] : ['Session']),
    }),
    logout: build.mutation<void, void>({
      query: () => ({ url: '/auth/logout', method: 'POST' }),
      invalidatesTags: (_result, error) => (error ? [] : ['Session']),
    }),
    listWorkers: build.query<Page<Worker>, WorkerListParams>({
      query: (params) => ({ url: '/workers', params }),
      providesTags: ['Worker'],
    }),
    getWorker: build.query<Worker, number>({
      query: (id) => `/workers/${id}`,
      providesTags: ['Worker'],
    }),
    createWorker: build.mutation<CreateWorkerResponse, WorkerRequest>({
      query: (body) => ({ url: '/workers', method: 'POST', body }),
      invalidatesTags: (_result, error) => (error ? [] : [...dataTags]),
    }),
    updateWorker: build.mutation<void, { id: number; body: WorkerRequest }>({
      query: ({ id, body }) => ({ url: `/workers/${id}`, method: 'PUT', body }),
      invalidatesTags: (_result, error) => (error ? [] : [...dataTags]),
    }),
    listCoordinates: build.query<Page<Coordinates>, void>({
      query: () => ({ url: '/coordinates', params: choiceParams }),
      providesTags: ['Coordinates'],
    }),
    listLocations: build.query<Page<Location>, void>({
      query: () => ({ url: '/locations', params: choiceParams }),
      providesTags: ['Location'],
    }),
    listAddresses: build.query<Page<Address>, void>({
      query: () => ({ url: '/addresses', params: choiceParams }),
      providesTags: ['Address'],
    }),
    listOrganizations: build.query<Page<Organization>, void>({
      query: () => ({ url: '/organizations', params: choiceParams }),
      providesTags: ['Organization'],
    }),
    listPersons: build.query<Page<Person>, void>({
      query: () => ({ url: '/persons', params: choiceParams }),
      providesTags: ['Person'],
    }),
  }),
})

export const {
  useGetSessionQuery,
  useRegisterMutation,
  useLoginMutation,
  useLogoutMutation,
  useListWorkersQuery,
  useGetWorkerQuery,
  useCreateWorkerMutation,
  useUpdateWorkerMutation,
  useListCoordinatesQuery,
  useListLocationsQuery,
  useListAddressesQuery,
  useListOrganizationsQuery,
  useListPersonsQuery,
} = api
