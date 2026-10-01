import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useAuth } from '../../context/AuthContext';
import ProtectedRoute from './ProtectedRoute';

vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

function renderRoute({ requireAdmin = false } = {}) {
  return render(
    <MemoryRouter initialEntries={['/secret']}>
      <Routes>
        <Route path="/login" element={<p>page de connexion</p>} />
        <Route path="/dashboard" element={<p>tableau de bord</p>} />
        <Route
          path="/secret"
          element={
            <ProtectedRoute requireAdmin={requireAdmin}>
              <p>contenu protégé</p>
            </ProtectedRoute>
          }
        />
      </Routes>
    </MemoryRouter>
  );
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    useAuth.mockReset();
  });

  it('should show a loading message while the session is being checked', () => {
    useAuth.mockReturnValue({ user: null, loading: true });

    renderRoute();

    expect(screen.getByText('chargement…')).toBeInTheDocument();
    expect(screen.queryByText('contenu protégé')).not.toBeInTheDocument();
  });

  it('should redirect to the login page when nobody is logged in', () => {
    useAuth.mockReturnValue({ user: null, loading: false });

    renderRoute();

    expect(screen.getByText('page de connexion')).toBeInTheDocument();
    expect(screen.queryByText('contenu protégé')).not.toBeInTheDocument();
  });

  it('should render the content when a user is logged in', () => {
    useAuth.mockReturnValue({ user: { role: 'user' }, loading: false });

    renderRoute();

    expect(screen.getByText('contenu protégé')).toBeInTheDocument();
  });

  it('should redirect a simple user to the dashboard when the route requires admin', () => {
    useAuth.mockReturnValue({ user: { role: 'user' }, loading: false });

    renderRoute({ requireAdmin: true });

    expect(screen.getByText('tableau de bord')).toBeInTheDocument();
    expect(screen.queryByText('contenu protégé')).not.toBeInTheDocument();
  });

  it('should render the content when an admin visits an admin route', () => {
    useAuth.mockReturnValue({ user: { role: 'admin' }, loading: false });

    renderRoute({ requireAdmin: true });

    expect(screen.getByText('contenu protégé')).toBeInTheDocument();
  });

  it('should redirect to the login page when nobody is logged in on an admin route', () => {
    useAuth.mockReturnValue({ user: null, loading: false });

    renderRoute({ requireAdmin: true });

    expect(screen.getByText('page de connexion')).toBeInTheDocument();
  });
});
