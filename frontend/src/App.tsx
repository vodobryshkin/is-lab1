import { useGetSessionQuery, useLogoutMutation } from './api/api'
import { errorText, isUnauthorized } from './api/errors'
import AuthPage from './pages/AuthPage'
import WorkersPage from './pages/WorkersPage'

function App() {
  const session = useGetSessionQuery()
  const [logout, logoutState] = useLogoutMutation()

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
    content = <WorkersPage />
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
