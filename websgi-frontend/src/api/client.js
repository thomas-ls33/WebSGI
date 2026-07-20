const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

async function request(path, options = {}, token) {
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(`${BASE_URL}${path}`, { ...options, headers });

  if (!response.ok) {
    const message = await response.text().catch(() => '');
    throw new Error(message || `Erreur ${response.status}`);
  }

  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) return response.json();
  return null;
}

export const api = {
  get: (path, token) => request(path, { method: 'GET' }, token),
  post: (path, body, token) => request(path, { method: 'POST', body: JSON.stringify(body) }, token),
  put: (path, body, token) => request(path, { method: 'PUT', body: JSON.stringify(body) }, token),
  patch: (path, body, token) => request(path, { method: 'PATCH', body: JSON.stringify(body) }, token),
  del: (path, token) => request(path, { method: 'DELETE' }, token),
};
