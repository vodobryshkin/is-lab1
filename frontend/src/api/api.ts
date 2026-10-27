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

interface CsrfTokenResponse {
  csrf_token: string
}

interface IdResponse {
  id: number
}

const rawBaseQuery = fetchBaseQuery({ baseUrl: '/api/v1', credentials: 'include' })

// Токен текущей сессии хранится только в памяти. Сервер меняет его после входа и выхода,
// поэтому тогда он сбрасывается и перед следующим изменяющим запросом запрашивается заново.
let csrfToken: string | null = null

const baseQuery: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (
  args,
  api,
  extraOptions,
) => {
  const request = typeof args === 'string' ? { url: args } : args
  if (!request.method || request.method === 'GET') {
    return rawBaseQuery(request, api, extraOptions)
  }

  if (!csrfToken) {
    const csrf = await rawBaseQuery('/auth/csrf', api, extraOptions)
    if (csrf.error) {
      return csrf
    }
    csrfToken = (csrf.data as CsrfTokenResponse).csrf_token
  }

  const result = await rawBaseQuery(
    { ...request, headers: { ...request.headers, 'X-CSRF-Token': csrfToken } },
    api,
    extraOptions,
  )
  if (result.error?.status === 403 || request.url === '/auth/login' || request.url === '/auth/logout') {
    csrfToken = null
  }
  return result
}

export const api = createApi({
  baseQuery,
  tagTypes: ['Session'],
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
  }),
})

export const { useGetSessionQuery, useRegisterMutation, useLoginMutation, useLogoutMutation } = api
