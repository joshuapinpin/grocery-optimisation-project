import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Login.css'
import GoogleSignInButton from '../components/GoogleSignInButton'

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const navigate = useNavigate()
  const { refreshUser } = useAuth()

  async function handleSubmit(e: React.SubmitEvent) {
    e.preventDefault()

    if (email == '' || password == '') { setErrorMsg('please fill everything in'); return}

    try{
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email: email, password: password })
      })

      if(!res.ok) { setErrorMsg('invalid email or password'); return }

      await refreshUser()
      navigate('/')
    } catch{
      setErrorMsg('could not reach server')
    }
  }

  return (
    <div className='login-page'>
      <div className='login-card'>
        <h1 className='login-title'>Welcome back</h1>
        <p className='login-subtitle'>Sign in to BagnSave to compare prices</p>

        <form onSubmit={handleSubmit} className='login-form'>
          <input
              type='text'
              placeholder='Email'
              value={email}
              onChange={(e) => setEmail(e.target.value)}
          />
          <input
              type='password'
              placeholder='Password'
              value={password}
              onChange={(e) => setPassword(e.target.value)}
          />
          {errorMsg != '' ? <p className='login-error'>{errorMsg}</p> : null}
          <button type='submit' className='login-button'>Sign in</button>
        </form>

        <div className='login-divider'>
          <span>or</span>
        </div>
        <GoogleSignInButton label='Sign in with Google' />

        <p className='login-register-link'>
          Don't have an account? <Link to='/register'>Create one</Link>
        </p>
      </div>
    </div>
  )
}

export default Login

