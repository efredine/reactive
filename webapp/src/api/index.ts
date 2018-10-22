export async function fetchJSON<T>(url: string, body?: any) {
  const request: RequestInit = { headers: { Accept: "application/json", }, method: body ? "POST" : "GET", body }
  const response = await fetch("http://api.icndb.com/jokes/random", request)
  const result = await response.json() as T
  return result
}

export async function fetchJSONwithCredentials<T>(url: string, body?: any) {
  const request: RequestInit = {
    body ,
    credentials: 'include',
    headers: {
      Accept: "application/json",
    },
    method: body ? "POST" : "GET",
  }
  const response = await fetch(url, request)
  const result = await response.json() as T
  return result
}
