import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, catchError, of, map, filter, take } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api/auth';
  private readonly SESSION_MARKER = 'sbvia_session_active';

  // The access token is kept in memory, not in localStorage (security rule for Delivery 1B)
  private accessToken = signal<string | null>(null);

  // Profile of the authenticated user
  public currentUser = signal<any>(null);

  /**
   * Tells whether the session restore attempt at start-up has finished
   * (whether it succeeded or not). The guard waits for this to be `true`
   * before evaluating isAuthenticated(), avoiding the race on refresh.
   */
  private sessionReadySubject = new BehaviorSubject<boolean>(false);
  public sessionReady$ = this.sessionReadySubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, credentials, { withCredentials: true }).pipe(
      tap((response: any) => {
        this.accessToken.set(response.accessToken);
        this.currentUser.set(response.user);
        localStorage.setItem(this.SESSION_MARKER, 'true');
        // After a manual login the session is also marked as ready
        this.sessionReadySubject.next(true);
      })
    );
  }

  updateProfile(data: any): Observable<any> {
    return this.http.put(`/api/users/me`, data, { withCredentials: true }).pipe(
      tap((response: any) => {
        // Update the current user signal
        this.currentUser.set(response);
      })
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/registro`, data, { withCredentials: true }).pipe(
      tap((response: any) => {
        this.accessToken.set(response.accessToken);
        this.currentUser.set(response.user);
        localStorage.setItem(this.SESSION_MARKER, 'true');
        this.sessionReadySubject.next(true);
      })
    );
  }

  /**
   * Restores the session only when the browser records a previous sign-in.
   * The marker holds no credentials; the refresh token stays in its HttpOnly cookie.
   */
  initializeSession(): Observable<boolean> {
    if (localStorage.getItem(this.SESSION_MARKER) !== 'true') {
      this.sessionReadySubject.next(true);
      return of(false);
    }

    return this.refreshSession();
  }

  /**
   * Tries to restore the session using the HttpOnly refresh-token cookie.
   * Called by APP_INITIALIZER when the application starts.
   * On completion, success or failure, it emits on sessionReady$ to unblock authGuard.
   */
  refreshSession(): Observable<boolean> {
    return this.http.post(`${this.API_URL}/refresh`, {}, { withCredentials: true }).pipe(
      tap((response: any) => {
        this.accessToken.set(response.accessToken);
        this.currentUser.set(response.user);
      }),
      map(() => true),
      catchError(() => {
        // No valid cookie: the user was not signed in. That is normal.
        this.accessToken.set(null);
        this.currentUser.set(null);
        localStorage.removeItem(this.SESSION_MARKER);
        return of(false);
      }),
      tap(() => {
        // The session is always marked as resolved when it finishes,
        // whether the refresh succeeded or not.
        this.sessionReadySubject.next(true);
      })
    );
  }

  logout(callApi = true): void {
    if (callApi && this.accessToken()) {
      // Call the API to send the token to the Redis blacklist
      this.http.post(`${this.API_URL}/logout`, {}, {
        headers: { Authorization: `Bearer ${this.accessToken()}` },
        withCredentials: true
      }).subscribe({
        next: () => this.clearSession(),
        error: () => this.clearSession()
      });
    } else {
      this.clearSession();
    }
  }

  private clearSession(navigate = true): void {
    this.accessToken.set(null);
    this.currentUser.set(null);
    localStorage.removeItem(this.SESSION_MARKER);
    // On sign-out the ready signal is reset for the next cycle
    this.sessionReadySubject.next(false);
    if (navigate) {
      this.router.navigate(['/login']);
    }
  }

  getAccessToken(): string | null {
    return this.accessToken();
  }

  isAuthenticated(): boolean {
    return this.accessToken() !== null;
  }

  /**
   * Waits for the session to be resolved and then returns whether the user
   * is authenticated. Used by authGuard to avoid race conditions.
   */
  waitForSessionAndCheck(): Observable<boolean> {
    return this.sessionReady$.pipe(
      filter(ready => ready === true),
      take(1),
      map(() => this.isAuthenticated())
    );
  }
}
