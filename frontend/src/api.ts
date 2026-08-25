const BASE_URL = '/api/api'

export const apiFetch = (path: string, options?: RequestInit) => {
    return fetch(`${BASE_URL}${path}`, options)
}