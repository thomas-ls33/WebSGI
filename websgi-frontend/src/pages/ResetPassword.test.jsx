import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { api } from '../api/client';
import ResetPassword from './ResetPassword';

vi.mock('../api/client', () => ({
  api: { post: vi.fn() },
}));

function renderResetPassword(url = '/reset-password?token=jeton-de-reinitialisation') {
  return render(
    <MemoryRouter initialEntries={[url]}>
      <Routes>
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/login" element={<p>page de connexion</p>} />
      </Routes>
    </MemoryRouter>
  );
}

async function fillAndSubmit(user, { password = 'nouveau-mdp-123', confirmPassword = 'nouveau-mdp-123' } = {}) {
  await user.type(screen.getByLabelText(/^nouveau mot de passe/i), password);
  await user.type(screen.getByLabelText(/^confirmer le mot de passe/i), confirmPassword);
  await user.click(screen.getByRole('button', { name: 'Réinitialiser' }));
}

describe('ResetPassword', () => {
  beforeEach(() => {
    api.post.mockReset();
  });

  it('should send the token from the url with the new password', async () => {
    api.post.mockResolvedValue(null);
    const user = userEvent.setup();
    renderResetPassword();

    await fillAndSubmit(user);

    expect(api.post).toHaveBeenCalledWith('/auth/reset-password', {
      token: 'jeton-de-reinitialisation',
      password: 'nouveau-mdp-123',
    });
  });

  it('should go to the login page once the password is reset', async () => {
    api.post.mockResolvedValue(null);
    const user = userEvent.setup();
    renderResetPassword();

    await fillAndSubmit(user);

    expect(await screen.findByText('page de connexion')).toBeInTheDocument();
  });

  it('should refuse to submit when the passwords do not match', async () => {
    const user = userEvent.setup();
    renderResetPassword();

    await fillAndSubmit(user, { confirmPassword: 'different-123' });

    expect(screen.getByText('Les mots de passe ne correspondent pas.')).toBeInTheDocument();
    expect(api.post).not.toHaveBeenCalled();
  });

  it('should display the error when the token is invalid or expired', async () => {
    api.post.mockRejectedValue(new Error('Lien de réinitialisation invalide ou expiré'));
    const user = userEvent.setup();
    renderResetPassword();

    await fillAndSubmit(user);

    expect(await screen.findByText('Lien de réinitialisation invalide ou expiré')).toBeInTheDocument();
    expect(screen.queryByText('page de connexion')).not.toBeInTheDocument();
  });

  it('should send an empty token when the url has none', async () => {
    api.post.mockResolvedValue(null);
    const user = userEvent.setup();
    renderResetPassword('/reset-password');

    await fillAndSubmit(user);

    expect(api.post).toHaveBeenCalledWith('/auth/reset-password', {
      token: '',
      password: 'nouveau-mdp-123',
    });
  });
});
