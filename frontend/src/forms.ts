import type {
  Address,
  AddressRequest,
  IdRef,
  Location,
  LocationFields,
  Person,
  PersonWriteRequest,
  Worker,
  WorkerRequest,
} from './api/api'

// Поля формы хранятся строками, как их отдают input. Значения всех вариантов
// (новый объект / существующий) сохраняются, поэтому переключение режима ничего не стирает.

export interface LocationState {
  mode: 'none' | 'new' | 'existing'
  id: string
  x: string
  y: string
  z: string
}

export interface AddressState {
  mode: 'new' | 'existing'
  id: string
  street: string
  town: LocationState
}

export interface PersonState {
  eyeColor: string
  hairColor: string
  birthday: string
  weight: string
  nationality: string
  location: LocationState
}

export interface WorkerState {
  name: string
  salary: string
  rating: string
  startDate: string
  position: string
  status: string
  coordinates: { mode: 'new' | 'existing'; id: string; x: string; y: string }
  person: PersonState & { mode: 'none' | 'new' | 'existing'; id: string }
  organization: {
    mode: 'none' | 'existing' | 'new'
    id: string
    fullName: string
    annualTurnover: string
    employeesCount: string
    rating: string
    type: string
    official: AddressState
    postal: AddressState
  }
}

export const emptyLocation: LocationState = { mode: 'new', id: '', x: '', y: '', z: '' }

export const emptyAddress: AddressState = {
  mode: 'new',
  id: '',
  street: '',
  town: { ...emptyLocation, mode: 'none' },
}

export const emptyPerson: PersonState = {
  eyeColor: '',
  hairColor: 'BLACK',
  birthday: '',
  weight: '',
  nationality: 'RUSSIA',
  location: emptyLocation,
}

export const emptyWorker: WorkerState = {
  name: '',
  salary: '',
  rating: '',
  startDate: '',
  position: '',
  status: '',
  coordinates: { mode: 'new', id: '', x: '', y: '' },
  person: { ...emptyPerson, mode: 'none', id: '' },
  organization: {
    mode: 'none',
    id: '',
    fullName: '',
    annualTurnover: '',
    employeesCount: '',
    rating: '',
    type: '',
    official: emptyAddress,
    postal: emptyAddress,
  },
}

// datetime-local ожидает локальное время без часового пояса: 2026-10-28T10:30
export function toLocalInput(iso: string): string {
  const date = new Date(iso)
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000)
  return local.toISOString().slice(0, 16)
}

export function locationStateFrom(location: Location | null): LocationState {
  if (!location) {
    return { ...emptyLocation, mode: 'none' }
  }
  return { mode: 'existing', id: String(location.id), x: String(location.x), y: String(location.y), z: String(location.z) }
}

export function addressStateFrom(address: Address): AddressState {
  return { mode: 'existing', id: String(address.id), street: address.street, town: locationStateFrom(address.town) }
}

export function personStateFrom(person: Person): PersonState {
  return {
    eyeColor: person.eye_color ?? '',
    hairColor: person.hair_color,
    birthday: toLocalInput(person.birthday).slice(0, 10),
    weight: String(person.weight),
    nationality: person.nationality,
    location: locationStateFrom(person.location),
  }
}

export function workerStateFrom(worker: Worker): WorkerState {
  return {
    ...emptyWorker,
    name: worker.name,
    salary: String(worker.salary),
    rating: String(worker.rating),
    startDate: toLocalInput(worker.start_date),
    position: worker.position ?? '',
    status: worker.status ?? '',
    coordinates: {
      mode: 'existing',
      id: String(worker.coordinates.id),
      x: String(worker.coordinates.x),
      y: String(worker.coordinates.y),
    },
    person: worker.person
      ? { ...emptyWorker.person, mode: 'existing', id: String(worker.person.id) }
      : emptyWorker.person,
    organization: worker.organization
      ? { ...emptyWorker.organization, mode: 'existing', id: String(worker.organization.id) }
      : emptyWorker.organization,
  }
}

export function locationRequest(location: LocationState): LocationFields | IdRef {
  if (location.mode === 'existing') {
    return { id: Number(location.id) }
  }
  return { x: Number(location.x), y: Number(location.y), z: Number(location.z) }
}

export function addressRequest(address: AddressState): AddressRequest {
  return {
    street: address.street,
    town: address.town.mode === 'none' ? null : locationRequest(address.town),
  }
}

export function personRequest(person: PersonState): PersonWriteRequest {
  return {
    eye_color: person.eyeColor || null,
    hair_color: person.hairColor,
    location: locationRequest(person.location),
    birthday: new Date(`${person.birthday}T00:00`).toISOString(),
    weight: Number(person.weight),
    nationality: person.nationality,
  }
}

export function workerRequest(state: WorkerState, forUpdate: boolean): WorkerRequest {
  const { coordinates, person, organization } = state
  const body: WorkerRequest = {
    name: state.name,
    coordinates:
      coordinates.mode === 'existing'
        ? { id: Number(coordinates.id) }
        : { x: Number(coordinates.x), y: Number(coordinates.y) },
    salary: Number(state.salary),
    rating: Number(state.rating),
    start_date: new Date(state.startDate).toISOString(),
    position: state.position || null,
    status: state.status || null,
    person: null,
  }

  if (person.mode === 'existing') {
    body.person = { id: Number(person.id) }
  } else if (person.mode === 'new') {
    body.person = personRequest(person)
  }

  if (organization.mode === 'existing') {
    body.organization_id = Number(organization.id)
  } else if (organization.mode === 'new') {
    body.organization = {
      full_name: organization.fullName,
      annual_turnover: Number(organization.annualTurnover),
      employees_count: Number(organization.employeesCount),
      rating: Number(organization.rating),
      type: organization.type || null,
    }
    if (organization.official.mode === 'existing') {
      body.organization.official_address_id = Number(organization.official.id)
    } else {
      body.organization.official_address = addressRequest(organization.official)
    }
    if (organization.postal.mode === 'existing') {
      body.organization.postal_address_id = Number(organization.postal.id)
    } else {
      body.organization.postal_address = addressRequest(organization.postal)
    }
  } else if (forUpdate) {
    // В PUT поле organization_id обязательно: null отвязывает организацию.
    body.organization_id = null
  }

  return body
}
