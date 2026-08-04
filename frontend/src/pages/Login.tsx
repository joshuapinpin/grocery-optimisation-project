import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import './Login.css'

function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const navigate = useNavigate()

  function handleSubmit(e: React.SubmitEvent) {
    e.preventDefault()
    console.log(username, password)

    if (username == '' || password == '') {
      setErrorMsg('please fill everything in')
      return
    }

    setErrorMsg('')
    // TODO connect to backend later
    navigate('/')
  }

  return (
    <div className='login-page'>
      <div className='login-card'>
        <h1 className='login-title'>Welcome back</h1>
        <p className='login-subtitle'>Sign in to BagnSave to compare prices</p>

        <form onSubmit={handleSubmit} className='login-form'>
          <input
            type='text'
            placeholder='Username'
            value={username}
            onChange={(e) => {
              setUsername(e.target.value)
            }}
          />

          <input
            type='password'
            placeholder='Password'
            value={password}
            onChange={(e) => {
              setPassword(e.target.value)
            }}
          />

          {errorMsg != '' ? <p className='login-error'>{errorMsg}</p> : null}

          <button type='submit' className='login-button'>Sign in</button>
        </form>

        <p className='login-register-link'>
          Don't have an account? <Link to='/register'>Create one</Link>
        </p>
      </div>
    </div>
  )
}

export default Login

