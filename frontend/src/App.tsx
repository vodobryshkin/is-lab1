import { useEffect, useState } from 'react'
import { useGetSessionQuery, useLogoutMutation } from './api/api'
import { errorText, isUnauthorized } from './api/errors'
import AddressesPage from './pages/AddressesPage'
import AuthPage from './pages/AuthPage'
import CoordinatesPage from './pages/CoordinatesPage'
import CreateWorkerPage from './pages/CreateWorkerPage'
import LocationsPage from './pages/LocationsPage'
import OrganizationsPage from './pages/OrganizationsPage'
import PersonsPage from './pages/PersonsPage'
import WorkersPage from './pages/WorkersPage'

function App() {
  const session = useGetSessionQuery()
  const [logout, logoutState] = useLogoutMutation()
  const [hash, setHash] = useState(window.location.hash)

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
  ]
  const currentPage = pages.find((page) => page.hash === hash) ?? pages[0]

  const loggedIn = !!session.data && !session.error

  let content
  if (session.isLoading) {
    content = <p>Загрузка…</p>
  } else if (isUnauthorized(session.error)) {
    content = <AuthPage />
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
              <button type="button" onClick={() => logout()} disabled={logoutState.isLoading}>
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
