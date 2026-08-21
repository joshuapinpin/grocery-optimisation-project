import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Account.css'

function Account() {
    const { user, logout } = useAuth()
    const [tab, setTab] = useState<'profile' | 'lists'>('profile')
    const navigate = useNavigate()

    async function handleLogout() {
        await logout()
        navigate('/')
    }

    return (
        <div className="account-page">
            <div className="account-tabs">
                <button
                    className={tab === 'profile' ? 'account-tab account-tab-active' : 'account-tab'}
                    onClick={() => setTab('profile')}
                >
                    Profile & Security
                </button>
                <button
                    className={tab === 'lists' ? 'account-tab account-tab-active' : 'account-tab'}
                    onClick={() => setTab('lists')}
                >
                    My Shopping Lists
                </button>
            </div>

            {tab === 'profile' && (
                <div className="account-panel">
                    <h2>Profile</h2>
                    <p><strong>Name:</strong> {user?.name}</p>
                    <p><strong>Email:</strong> {user?.email}</p>
                    {/* TODO: once AccountDTO exposes authProvider, show a
             "Signed in with Google" badge and hide password fields */}
                    <button className="account-logout-btn" onClick={handleLogout}>
                        Log Out
                    </button>
                </div>
            )}

            {tab === 'lists' && (
                <div className="account-panel">
                    <h2>My Shopping Lists</h2>
                    <p>Shopping list management goes here — wire up to /api/shopping-lists.</p>
                </div>
            )}
        </div>
    )
}

export default Account