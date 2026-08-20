import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import './Register.css'

function Register() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPass, setConfirmPass] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const navigate = useNavigate()

  async function handleSubmit(e: React.SubmitEvent) {
    e.preventDefault()
    console.log(name, password)

    if (password != confirmPass) {setErrorMsg("passwords dont match"); return}
    if (name == '' || password == '' || email == '') {setErrorMsg("please fill everything in"); return}


    try{
      const res = await fetch('api/auth/register',{
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include', // needed to receive the session cookie
        body: JSON.stringify({
          email: email,
          password: password,
          name: name
        })
      })

      if(!res.ok){
        const err = await res.json().catch(() => null)
        setErrorMsg(err?.error ?? 'registration failed')
        return
      }
      navigate('/login')
    }
    catch{
      setErrorMsg('could not reach server')
    }
  }

  return (
    <div className='register-page'>
      <div className='register-card'>
        <h1 className='register-title'>Create your account</h1>
        <p className='register-subtitle'>Join BagnSave and start saving today</p>

        <form onSubmit={handleSubmit} className='register-form'>
          <input
            type='text'
            placeholder='Name'
            value={name}
            onChange={(e) => {
              setName(e.target.value)
            }}
          />

          <input
              type='email'
              placeholder='Email'
              value={email}
              onChange={(e) => {
                setEmail(e.target.value)
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

          <input
            type='password'
            placeholder='Confirm password'
            value={confirmPass}
            onChange={(e) => {
              setConfirmPass(e.target.value)
            }}
          />

          {errorMsg != '' ? <p className='register-error'>{errorMsg}</p> : null}

          <button type='submit' className='register-button'>Register</button>
        </form>

        <p className='register-login-link'>
          Already have an account? <Link to='/login'>Sign in</Link>
        </p>
      </div>
    </div>
  )
}

export default Register