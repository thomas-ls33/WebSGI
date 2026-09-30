import { act, renderHook, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { api } from '../api/client';
import { AuthProvider, useAuth } from './AuthContext';

vi.mock('../api/client', () => ({
  api: { get: vi.fn(), post: vi.fn() },
}));

const TOKEN_KEY = 'websgi_token';
const ALICE = { id: 1, fullName: 'Alice Martin', email: 'alice@example.com', role: 'user' };

function renderAuth() {
  return renderHook(() => useAuth(), { wrapper: AuthProvider });
}

describe('AuthContext', () => {
  beforeEach(() => {
    api.get.mockReset();
    api.post.mockReset();
    api.get.mockResolvedValue(ALICE);
  });

  describe('initial state', () => {
    it('should be anonymous and not loading when no token is stored', async () => {
      const { result } = renderAuth();

      await waitFor(() => expect(result.current.loading).toBe(false));
      expect(result.current.user).toBeNull();
      expect(result.current.token).toBeNull();
      expect(api.get).not.toHaveBeenCalled();
    });

    it('should restore the user from the stored token', async () => {
      localStorage.setItem(TOKEN_KEY, 'jeton-stocke');

      const { result } = renderAuth();

      await waitFor(() => expect(result.current.user).toEqual(ALICE));
      expect(api.get).toHaveBeenCalledWith('/auth/me', 'jeton-stocke');
      expect(result.current.loading).toBe(false);
    });

    it('should be loading until the stored token has been checked', async () => {
      localStorage.setItem(TOKEN_KEY, 'jeton-stocke');

      const { result } = renderAuth();

      expect(result.current.loading).toBe(true);
      await waitFor(() => expect(result.current.loading).toBe(false));
    });

    it('should drop the stored token when the server rejects it', async () => {
      localStorage.setItem(TOKEN_KEY, 'jeton-expire');
      api.get.mockRejectedValue(new Error('Authentification requise'));

      const { result } = renderAuth();

      await waitFor(() => expect(result.current.loading).toBe(false));
      expect(result.current.user).toBeNull();
      expect(result.current.token).toBeNull();
      expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
    });
  });

  describe('login', () => {
    it('should store the token and expose the user when credentials are valid', async () => {
      api.post.mockResolvedValue({ token: 'nouveau-jeton', user: ALICE });
      const { result } = renderAuth();
      await waitFor(() => expect(result.current.loading).toBe(false));

      let returnedUser;
      await act(async () => {
        returnedUser = await result.current.login('alice@example.com', 'motdepasse123');
      });

      expect(api.post).toHaveBeenCalledWith('/auth/login', {
        email: 'alice@example.com',
        password: 'motdepasse123',
      });
      expect(returnedUser).toEqual(ALICE);
      expect(result.current.user).toEqual(ALICE);
      expect(result.current.token).toBe('nouveau-jeton');
      expect(localStorage.getItem(TOKEN_KEY)).toBe('nouveau-jeton');
    });

    it('should propagate the error and stay anonymous when credentials are invalid', async () => {
      api.post.mockRejectedValue(new Error('Email ou mot de passe incorrect'));
      const { result } = renderAuth();
      await waitFor(() => expect(result.current.loading).toBe(false));

      await act(async () => {
        await expect(result.current.login('alice@example.com', 'mauvais')).rejects.toThrow(
          'Email ou mot de passe incorrect'
        );
      });

      expect(result.current.user).toBeNull();
      expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
    });
  });

  describe('register', () => {
    it('should create the account and log the user in', async () => {
      const payload = { fullName: 'Alice Martin', email: 'alice@example.com', password: 'motdepasse123' };
      api.post.mockResolvedValue({ token: 'jeton-inscription', user: ALICE });
      const { result } = renderAuth();
      await waitFor(() => expect(result.current.loading).toBe(false));

      await act(async () => {
        await result.current.register(payload);
      });

      expect(api.post).toHaveBeenCalledWith('/auth/register', payload);
      expect(result.current.user).toEqual(ALICE);
      expect(localStorage.getItem(TOKEN_KEY)).toBe('jeton-inscription');
    });
  });

  describe('logout', () => {
    it('should clear the session and the stored token', async () => {
      localStorage.setItem(TOKEN_KEY, 'jeton-stocke');
      const { result } = renderAuth();
      await waitFor(() => expect(result.current.user).toEqual(ALICE));

      act(() => {
        result.current.logout();
      });

      expect(result.current.user).toBeNull();
      expect(result.current.token).toBeNull();
      expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
    });
  });
});
