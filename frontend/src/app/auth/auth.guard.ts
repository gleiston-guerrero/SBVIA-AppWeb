import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { map } from 'rxjs';

/**
 * Asynchronous authentication guard.
 *
 * It waits for APP_INITIALIZER to finish trying to restore the session
 * (sessionReady$) before deciding whether the user is authenticated.
 * This avoids the race where the guard redirects to login
 * before the token refresh has completed.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.waitForSessionAndCheck().pipe(
    map(isAuthenticated => {
      if (isAuthenticated) {
        return true;
      }
      // No session after trying to restore it: redirect to login
      return router.createUrlTree(['/login']);
    })
  );
};
