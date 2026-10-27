import { useGetSessionQuery, useLogoutMutation } from './api/api'
import { errorText, isUnauthorized } from './api/errors'
import AuthPage from './pages/AuthPage'

function App() {
  const session = useGetSessionQuery()
  const [logout, logoutState] = useLogoutMutation()

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
    content = (
      <p>
        Вы вошли как <strong>{session.data.username}</strong> (ID пользователя {session.data.id}).
      </p>
    )
  }

  return (
    <>
      <header>
        <section>
          <h1>Работники</h1>
          {session.data && !session.error && (
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
