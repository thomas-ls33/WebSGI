import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { api } from '../api/client';
import ForgotPassword from './ForgotPassword';

vi.mock('../api/client', () => ({
  api: { post: vi.fn() },
}));

function renderForgotPassword() {
  return render(
    <MemoryRouter>
      <ForgotPassword />
    </MemoryRouter>
  );
}

async function submitEmail(user, email = 'alice@example.com') {
  await user.type(screen.getByLabelText(/^email/i), email);
  await user.click(screen.getByRole('button', { name: 'Envoyer le lien' }));
}

describe('ForgotPassword', () => {
  beforeEach(() => {
    api.post.mockReset();
  });

  it('should send the typed email to the API', async () => {
    api.post.mockResolvedValue(null);
    const user = userEvent.setup();
    renderForgotPassword();

    await submitEmail(user);

    expect(api.post).toHaveBeenCalledWith('/auth/forgot-password', { email: 'alice@example.com' });
  });

  it('should show a neutral confirmation and hide the form once sent', async () => {
    api.post.mockResolvedValue(null);
    const user = userEvent.setup();
    renderForgotPassword();

    await submitEmail(user);

    expect(
      await screen.findByText(/Si un compte existe pour alice@example\.com/)
    ).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Envoyer le lien' })).not.toBeInTheDocument();
  });

  it('should display the error and keep the form when the request fails', async () => {
    api.post.mockRejectedValue(new Error('Une erreur inattendue est survenue'));
    const user = userEvent.setup();
    renderForgotPassword();

    await submitEmail(user);

    expect(await screen.findByText('Une erreur inattendue est survenue')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Envoyer le lien' })).toBeInTheDocument();
  });
});
