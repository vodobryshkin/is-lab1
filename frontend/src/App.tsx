import { useEffect, useState } from 'react'
import { useGetSessionQuery, useLogoutMutation } from './api/api'
import { errorText, isUnauthorized } from './api/errors'
import AuthPage from './pages/AuthPage'
import CreateWorkerPage from './pages/CreateWorkerPage'
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
    { hash: '', title: 'Список работников' },
    { hash: '#new-worker', title: 'Новый работник' },
  ]
  const currentHash = pages.some((page) => page.hash === hash) ? hash : ''

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
    content = currentHash === '#new-worker' ? <CreateWorkerPage /> : <WorkersPage />
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
                    {page.hash === currentHash ? <strong>{page.title}</strong> : <a href={page.hash || '#'}>{page.title}</a>}
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
