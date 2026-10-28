import { useEffect, useRef, useState } from 'react'
import { useDispatch } from 'react-redux'
import { api, dataTags, useGetSessionQuery, useLogoutMutation } from './api/api'
import { errorText, isUnauthorized } from './api/errors'
import AddressesPage from './pages/AddressesPage'
import AuthPage from './pages/AuthPage'
import CoordinatesPage from './pages/CoordinatesPage'
import CreateWorkerPage from './pages/CreateWorkerPage'
import LocationsPage from './pages/LocationsPage'
import OrganizationsPage from './pages/OrganizationsPage'
import PersonsPage from './pages/PersonsPage'
import SpecialPage from './pages/SpecialPage'
import WorkersPage from './pages/WorkersPage'

function App() {
  const session = useGetSessionQuery()
  const [logout, logoutState] = useLogoutMutation()
  const [hash, setHash] = useState(window.location.hash)
  const dispatch = useDispatch()
  const [eventsMessage, setEventsMessage] = useState('')
  const [reconnects, setReconnects] = useState(0)
  const [sessionEnded, setSessionEnded] = useState(false)
  const loggingOut = useRef(false)

  useEffect(() => {
    const updateHash = () => setHash(window.location.hash)
    window.addEventListener('hashchange', updateHash)
    return () => window.removeEventListener('hashchange', updateHash)
  }, [])

  const pages = [
    { hash: '', title: 'Список работников', content: <WorkersPage /> },
    { hash: '#new-worker', title: 'Новый работник', content: <CreateWorkerPage /> },
    { hash: '#organizations', title: 'Организации', content: <OrganizationsPage /> },
    { hash: '#coordinates', title: 'Координаты', content: <CoordinatesPage /> },
    { hash: '#locations', title: 'Локации', content: <LocationsPage /> },
    { hash: '#addresses', title: 'Адреса', content: <AddressesPage /> },
    { hash: '#persons', title: 'Персоны', content: <PersonsPage /> },
    { hash: '#special', title: 'Специальные операции', content: <SpecialPage /> },
  ]
  const currentPage = pages.find((page) => page.hash === hash) ?? pages[0]

  const loggedIn = !!session.data && !session.error
  const userId = loggedIn ? session.data.id : null

  // События сервера только сообщают, что данные изменились; сами данные перечитываются по REST.
  useEffect(() => {
    if (userId === null) {
      return
    }
    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
    const socket = new WebSocket(`${protocol}://${window.location.host}/api/v1/events`)
    let closedByUs = false

    socket.onmessage = (event) => {
      const message = JSON.parse(event.data) as { type: string; code?: string }
      if (message.type === 'AUTHENTICATED') {
        setEventsMessage('')
        setSessionEnded(false)
        dispatch(api.util.invalidateTags([...dataTags]))
      } else if (message.type === 'DATA_CHANGED') {
        dispatch(api.util.invalidateTags([...dataTags]))
      } else if (message.type === 'ERROR' && message.code === 'SESSION_ENDED') {
        closedByUs = true
        socket.close()
        if (!loggingOut.current) {
          setSessionEnded(true)
        }
        dispatch(api.util.invalidateTags(['Session']))
      }
    }
    socket.onclose = () => {
      if (!closedByUs) {
        setEventsMessage('Соединение для автоматического обновления данных потеряно.')
        dispatch(api.util.invalidateTags(['Session']))
      }
    }

    return () => {
      closedByUs = true
      socket.close()
    }
  }, [userId, reconnects, dispatch])

  async function handleLogout() {
    loggingOut.current = true
    await logout()
    loggingOut.current = false
  }

  let content
  if (session.isLoading) {
    content = <p>Загрузка…</p>
  } else if (isUnauthorized(session.error)) {
    content = (
      <>
        {sessionEnded && <p role="alert">Сессия завершена. Войдите снова.</p>}
        <AuthPage />
      </>
    )
  } else if (session.error) {
    content = (
      <>
        <p role="alert">{errorText(session.error)}</p>
        <button type="button" onClick={() => session.refetch()}>
          Повторить
        </button>
      </>
    )
  } else if (session.data) {
    content = currentPage.content
  }

  return (
    <>
      <header>
        <section className={loggedIn ? undefined : 'centered'}>
          <h1>Работники</h1>
          {loggedIn && (
            <nav>
              <span>{session.data.username}</span>
              <button type="button" onClick={handleLogout} disabled={logoutState.isLoading}>
                {logoutState.isLoading ? 'Выход…' : 'Выйти'}
              </button>
            </nav>
          )}
        </section>
        {loggedIn && (
          <section>
            <nav className="menu">
              <ul>
                {pages.map((page) => (
                  <li key={page.hash}>
                    {page === currentPage ? <strong>{page.title}</strong> : <a href={page.hash || '#'}>{page.title}</a>}
                  </li>
                ))}
              </ul>
            </nav>
          </section>
        )}
        {loggedIn && eventsMessage && (
          <section>
            <p role="alert">
              {eventsMessage}{' '}
              <button type="button" onClick={() => setReconnects(reconnects + 1)}>
                Подключиться снова
              </button>
            </p>
          </section>
        )}
        {logoutState.isError && (
          <section>
            <p role="alert">{errorText(logoutState.error)}</p>
          </section>
        )}
      </header>
      <main>
        <section>{content}</section>
      </main>
    </>
  )
}

export default App
