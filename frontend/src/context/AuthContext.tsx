import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'

interface User {
    id: number
    email: string
    name: string
    authProvider: string
}

interface AuthContextType {
    user: User | null
    isAuthenticated: boolean
    loading: boolean
    refreshUser: () => Promise<void>
    logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null)
    const [loading, setLoading] = useState(true)

    async function refreshUser() {
        try {
            const res = await fetch('/api/user', { credentials: 'include' })
            if (!res.ok) {
                setUser(null)
                return
            }
            const data = await res.json()
            // backend returns null body when not authenticated
            setUser(data ?? null)
        } catch {
            setUser(null)
        }
    }

    async function logout() {
        try {
            await fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include',
            })
        } catch {
            // even if the request fails, clear local state
        }
        setUser(null)
    }

    useEffect(() => {
        async function init() {
            setLoading(true)
            await refreshUser()
            setLoading(false)
        }
        init()
    }, [])

    const value: AuthContextType = {
        user,
        isAuthenticated: user !== null,
        loading,
        refreshUser,
        logout,
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const ctx = useContext(AuthContext)
    if (!ctx) {
        throw new Error('useAuth must be used within an AuthProvider')
    }
    return ctx
}