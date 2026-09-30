import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useAuth } from '../context/AuthContext';
import Login from './Login';

vi.mock('../context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

function renderLogin(initialEntry = '/login') {
  return render(
    <MemoryRouter initialEntries={[initialEntry]}>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<p>tableau de bord</p>} />
        <Route path="/admin" element={<p>espace admin</p>} />
        <Route path="/demande" element={<p>page de demande</p>} />
        <Route path="/register" element={<p>page d'inscription</p>} />
        <Route path="/forgot-password" element={<p>page mot de passe oublié</p>} />
      </Routes>
    </MemoryRouter>
  );
}

async function fillAndSubmit(user, email = 'alice@example.com', password = 'motdepasse123') {
  await user.type(screen.getByLabelText(/^email/i), email);
  await user.type(screen.getByLabelText(/^mot de passe/i), password);
  await user.click(screen.getByRole('button', { name: 'Se connecter' }));
}

describe('Login', () => {
  const login = vi.fn();

  beforeEach(() => {
    login.mockReset();
    useAuth.mockReturnValue({ login });
  });

  it('should call login with the typed credentials', async () => {
    login.mockResolvedValue({ role: 'user' });
    const user = userEvent.setup();
    renderLogin();

    await fillAndSubmit(user);

    expect(login).toHaveBeenCalledWith('alice@example.com', 'motdepasse123');
  });

  it('should redirect a simple user to the dashboard', async () => {
    login.mockResolvedValue({ role: 'user' });
    const user = userEvent.setup();
    renderLogin();

    await fillAndSubmit(user);

    expect(await screen.findByText('tableau de bord')).toBeInTheDocument();
  });

  it('should redirect an admin to the admin area', async () => {
    login.mockResolvedValue({ role: 'admin' });
    const user = userEvent.setup();
    renderLogin();

    await fillAndSubmit(user, 'admin@websgi.fr', 'admin123');

    expect(await screen.findByText('espace admin')).toBeInTheDocument();
  });

  it('should redirect to the page the user came from when there is one', async () => {
    login.mockResolvedValue({ role: 'user' });
    const user = userEvent.setup();
    renderLogin({ pathname: '/login', state: { from: '/demande' } });

    await fillAndSubmit(user);

    expect(await screen.findByText('page de demande')).toBeInTheDocument();
  });

  it('should display the error message when the login fails', async () => {
    login.mockRejectedValue(new Error('Email ou mot de passe incorrect'));
    const user = userEvent.setup();
    renderLogin();

    await fillAndSubmit(user);

    expect(await screen.findByText('Email ou mot de passe incorrect')).toBeInTheDocument();
    expect(screen.queryByText('tableau de bord')).not.toBeInTheDocument();
  });

  it('should display a default message when the error has no message', async () => {
    login.mockRejectedValue(new Error(''));
    const user = userEvent.setup();
    renderLogin();

    await fillAndSubmit(user);

    expect(await screen.findByText('Identifiants invalides.')).toBeInTheDocument();
  });

  it('should disable the submit button while the login is in progress', async () => {
    login.mockReturnValue(new Promise(() => {}));
    const user = userEvent.setup();
    renderLogin();

    await fillAndSubmit(user);

    expect(await screen.findByRole('button', { name: 'Connexion…' })).toBeDisabled();
  });

  it('should link to the forgotten password page', async () => {
    const user = userEvent.setup();
    renderLogin();

    await user.click(screen.getByRole('link', { name: 'Mot de passe oublié ?' }));

    expect(screen.getByText('page mot de passe oublié')).toBeInTheDocument();
  });
});
