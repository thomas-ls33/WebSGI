import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useAuth } from '../context/AuthContext';
import Register from './Register';

vi.mock('../context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

function renderRegister() {
  return render(
    <MemoryRouter initialEntries={['/register']}>
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<p>tableau de bord</p>} />
        <Route path="/login" element={<p>page de connexion</p>} />
      </Routes>
    </MemoryRouter>
  );
}

async function fillForm(user, { password = 'motdepasse123', confirmPassword = 'motdepasse123' } = {}) {
  await user.type(screen.getByLabelText(/^nom complet/i), 'Alice Martin');
  await user.type(screen.getByLabelText(/^email/i), 'alice@example.com');
  await user.type(screen.getByLabelText(/^mot de passe/i), password);
  await user.type(screen.getByLabelText(/^confirmer le mot de passe/i), confirmPassword);
  await user.click(screen.getByRole('button', { name: 'Créer mon compte' }));
}

describe('Register', () => {
  const register = vi.fn();

  beforeEach(() => {
    register.mockReset();
    useAuth.mockReturnValue({ register });
  });

  it('should register without the password confirmation and go to the dashboard', async () => {
    register.mockResolvedValue({ role: 'user' });
    const user = userEvent.setup();
    renderRegister();

    await fillForm(user);

    expect(register).toHaveBeenCalledWith({
      fullName: 'Alice Martin',
      email: 'alice@example.com',
      password: 'motdepasse123',
    });
    expect(await screen.findByText('tableau de bord')).toBeInTheDocument();
  });

  it('should refuse to register when the passwords do not match', async () => {
    const user = userEvent.setup();
    renderRegister();

    await fillForm(user, { confirmPassword: 'autre-mot-de-passe' });

    expect(screen.getByText('Les mots de passe ne correspondent pas.')).toBeInTheDocument();
    expect(register).not.toHaveBeenCalled();
  });

  it('should display the error message when the registration fails', async () => {
    register.mockRejectedValue(new Error('Un compte existe déjà avec l\'email alice@example.com'));
    const user = userEvent.setup();
    renderRegister();

    await fillForm(user);

    expect(
      await screen.findByText("Un compte existe déjà avec l'email alice@example.com")
    ).toBeInTheDocument();
    expect(screen.queryByText('tableau de bord')).not.toBeInTheDocument();
  });

  it('should clear the previous error when the form is submitted again', async () => {
    register.mockResolvedValue({ role: 'user' });
    const user = userEvent.setup();
    renderRegister();
    await fillForm(user, { confirmPassword: 'autre-mot-de-passe' });
    expect(screen.getByText('Les mots de passe ne correspondent pas.')).toBeInTheDocument();

    await user.clear(screen.getByLabelText(/^confirmer le mot de passe/i));
    await user.type(screen.getByLabelText(/^confirmer le mot de passe/i), 'motdepasse123');
    await user.click(screen.getByRole('button', { name: 'Créer mon compte' }));

    expect(await screen.findByText('tableau de bord')).toBeInTheDocument();
  });

  it('should link back to the login page', async () => {
    const user = userEvent.setup();
    renderRegister();

    await user.click(screen.getByRole('link', { name: 'Connectez-vous' }));

    expect(screen.getByText('page de connexion')).toBeInTheDocument();
  });
});
