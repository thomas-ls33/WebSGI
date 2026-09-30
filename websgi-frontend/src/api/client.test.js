import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { api } from './client';

function jsonResponse(body, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}

describe('api client', () => {
  let fetchMock;

  beforeEach(() => {
    fetchMock = vi.fn();
    vi.stubGlobal('fetch', fetchMock);
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  describe('requests', () => {
    it('should call the /api prefixed url with the GET method', async () => {
      fetchMock.mockResolvedValue(jsonResponse([]));

      await api.get('/requests/mine');

      const [url, options] = fetchMock.mock.calls[0];
      expect(url).toBe('/api/requests/mine');
      expect(options.method).toBe('GET');
    });

    it('should send a JSON content type', async () => {
      fetchMock.mockResolvedValue(jsonResponse({}));

      await api.get('/auth/me');

      expect(fetchMock.mock.calls[0][1].headers['Content-Type']).toBe('application/json');
    });

    it('should add the bearer token when a token is given', async () => {
      fetchMock.mockResolvedValue(jsonResponse({}));

      await api.get('/auth/me', 'mon-token');

      expect(fetchMock.mock.calls[0][1].headers.Authorization).toBe('Bearer mon-token');
    });

    it('should not send an Authorization header without token', async () => {
      fetchMock.mockResolvedValue(jsonResponse({}));

      await api.get('/auth/me');

      expect(fetchMock.mock.calls[0][1].headers).not.toHaveProperty('Authorization');
    });

    it('should serialize the body as JSON for POST requests', async () => {
      fetchMock.mockResolvedValue(jsonResponse({}));

      await api.post('/auth/login', { email: 'alice@example.com', password: 'secret' });

      const [, options] = fetchMock.mock.calls[0];
      expect(options.method).toBe('POST');
      expect(JSON.parse(options.body)).toEqual({ email: 'alice@example.com', password: 'secret' });
    });

    it.each([
      ['put', 'PUT'],
      ['patch', 'PATCH'],
    ])('should use the %s method with a JSON body', async (name, method) => {
      fetchMock.mockResolvedValue(jsonResponse({}));

      await api[name]('/admin/requests/1/status', { status: 'Validée' }, 'jeton');

      const [, options] = fetchMock.mock.calls[0];
      expect(options.method).toBe(method);
      expect(JSON.parse(options.body)).toEqual({ status: 'Validée' });
      expect(options.headers.Authorization).toBe('Bearer jeton');
    });

    it('should use the DELETE method', async () => {
      fetchMock.mockResolvedValue(new Response(null, { status: 204 }));

      await api.del('/requests/1', 'jeton');

      expect(fetchMock.mock.calls[0][1].method).toBe('DELETE');
    });
  });

  describe('responses', () => {
    it('should return the parsed body when the response is JSON', async () => {
      fetchMock.mockResolvedValue(jsonResponse({ id: 1, email: 'alice@example.com' }));

      const result = await api.get('/auth/me', 'jeton');

      expect(result).toEqual({ id: 1, email: 'alice@example.com' });
    });

    it('should return null when the response is not JSON', async () => {
      fetchMock.mockResolvedValue(new Response(null, { status: 200 }));

      const result = await api.post('/auth/forgot-password', { email: 'alice@example.com' });

      expect(result).toBeNull();
    });

    it('should throw the message field when the error body is JSON', async () => {
      fetchMock.mockResolvedValue(
        jsonResponse({ message: 'Email ou mot de passe incorrect' }, 401)
      );

      await expect(api.post('/auth/login', {})).rejects.toThrow(
        new Error('Email ou mot de passe incorrect')
      );
    });

    it('should throw the raw body when the error body is plain text', async () => {
      fetchMock.mockResolvedValue(new Response('Accès refusé', { status: 403 }));

      await expect(api.get('/admin/requests', 'jeton')).rejects.toThrow(new Error('Accès refusé'));
    });

    it('should throw the raw body when the JSON error has no message field', async () => {
      fetchMock.mockResolvedValue(jsonResponse({ error: 'boom' }, 500));

      await expect(api.get('/requests/mine', 'jeton')).rejects.toThrow(
        new Error('{"error":"boom"}')
      );
    });

    it('should throw a generic error with the status when the body is empty', async () => {
      fetchMock.mockResolvedValue(new Response('', { status: 500 }));

      await expect(api.get('/requests/mine', 'jeton')).rejects.toThrow('Erreur 500');
    });
  });
});
