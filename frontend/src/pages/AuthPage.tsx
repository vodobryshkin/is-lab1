import { useState } from 'react'
import type { FormEvent } from 'react'
import { useLoginMutation, useRegisterMutation } from '../api/api'
import { errorText, validationIssues } from '../api/errors'

function AuthPage() {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [login, loginState] = useLoginMutation()
  const [register, registerState] = useRegisterMutation()

  const current = mode === 'login' ? loginState : registerState
  const issues = validationIssues(current.error)
  const otherIssues = issues.filter((issue) => issue.field !== 'username' && issue.field !== 'password')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (mode === 'login') {
      await login({ username, password })
      return
    }
    const result = await register({ username, password })
    if (!result.error) {
      setMode('login')
      await login({ username, password })
    }
  }

  function switchMode() {
    setMode(mode === 'login' ? 'register' : 'login')
    loginState.reset()
    registerState.reset()
  }

  function fieldIssues(field: string) {
    return issues
      .filter((issue) => issue.field === field)
      .map((issue) => (
        <small key={issue.code} role="alert">
          {issue.message}
        </small>
      ))
  }

  return (
    <form onSubmit={handleSubmit} className="auth-form">
      <fieldset disabled={current.isLoading}>
        <legend>{mode === 'login' ? 'Вход' : 'Регистрация'}</legend>

        {current.isError && <p role="alert">{errorText(current.error)}</p>}
        {otherIssues.length > 0 && (
          <ul>
            {otherIssues.map((issue) => (
              <li key={issue.field + issue.code}>{issue.message}</li>
            ))}
          </ul>
        )}

        <label htmlFor="username">Логин</label>
        <input
          id="username"
          type="text"
          autoComplete="username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          required
          minLength={3}
          maxLength={64}
          pattern="[a-zA-Z0-9_]{3,64}"
          title="От 3 до 64 символов: латинские буквы, цифры и _"
        />
        <small>От 3 до 64 символов: латинские буквы, цифры и _.</small>
        {fieldIssues('username')}

        <label htmlFor="password">Пароль</label>
        <input
          id="password"
          type="password"
          autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          required
          minLength={6}
          maxLength={32}
          pattern="[a-zA-Z0-9_]{6,32}"
          title="От 6 до 32 символов: латинские буквы, цифры и _"
        />
        <small>От 6 до 32 символов: латинские буквы, цифры и _.</small>
        {fieldIssues('password')}

        <button type="submit">
          {current.isLoading ? 'Отправка…' : mode === 'login' ? 'Войти' : 'Зарегистрироваться'}
        </button>
      </fieldset>

      <p>
        {mode === 'login' ? 'Нет учётной записи?' : 'Уже зарегистрированы?'}{' '}
        <button type="button" onClick={switchMode}>
          {mode === 'login' ? 'Регистрация' : 'Вход'}
        </button>
      </p>
    </form>
  )
}

export default AuthPage
