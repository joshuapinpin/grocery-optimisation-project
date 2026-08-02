import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import './Register.css'

function Register() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPass, setConfirmPass] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const navigate = useNavigate()

  function handleSubmit(e: any) {
    e.preventDefault()
    console.log(username, password)

    if (password != confirmPass) {
      setErrorMsg("passwords dont match")
      return
    }

    if (username == '' || password == '') {
      setErrorMsg("please fill everything in")
      return
    }

    setErrorMsg('')
    // TODO connect to backend later
    navigate('/login')
  }

  return (
    <div className='register-page'>
      <div className='register-card'>
        <h1 className='register-title'>Create your account</h1>
        <p className='register-subtitle'>Join BagnSave and start saving today</p>

        <form onSubmit={handleSubmit} className='register-form'>
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