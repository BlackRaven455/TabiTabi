import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {StorageService} from './storage.service';
import {BehaviorSubject, catchError, map, Observable, of, tap, throwError} from 'rxjs';
import {jwtToken} from '../../../shared/types/jwtToken';
import {User} from '../../../shared/types/user';
import {UserPreference} from '../../../shared/types/user-preference';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private loginTracker ;
  loggedInStatus$ ;
  userPreference : UserPreference[] = [];
  constructor(private http: HttpClient, private ss: StorageService) {
    this.loginTracker = new BehaviorSubject(this.checkIfLoggedIn());
    this.loggedInStatus$= this.loginTracker.asObservable();
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${API_URL}/auth/login`, {email, password});
  }

  registerUser(user: User) {
    return this.http.post(`${API_URL}/auth/register`, user).pipe(
      tap({
        next: (res) => console.log('Registration success:', user),
        error: (err) => console.error('Registration failed:', err)
      }),
      map(() => true),
      catchError((err) => {
        return of(false);
      })
    );
  }

  persistUser(resp: jwtToken) {
    [
      ['loggedIn', 'true'],
      ['token', resp.token]
    ].forEach(item => this.ss.setItem(item[0], item[1]));
    this.loginTracker.next(true);
  }

  getUserPreference(): Observable<UserPreference[]> {
    return this.http.get<UserPreference[]>(`${API_URL}/users/me/preferences`, this.getAuthHeader()).pipe(
      tap(res => {
        console.log("UserPreference success:", res);
        this.userPreference = res;
      }),
      catchError(err => {
        console.log("UserPreference failed:", err);
        return of([]);
      })
    );
  }

  setUserPreference(placeId: number, isLiked: boolean): Observable<any> {
    return this.http.post(`${API_URL}/users/me/preferences`, {placeId, isLiked}, this.getAuthHeader())
      .pipe(
        catchError(error => {
          console.error('Error setting user preference:', error);
          return throwError(error);
        })
      );
  }


  getPersistedUser(): {email: string} {
    return {
      email: this.ss.getItem('userEmail') || ''
    };
  }

  getCurrentUser() {
    return this.http.get<User>( '/me', this.getAuthHeader()).pipe(
      tap(user => {
        this.ss.setItem('userEmail', user.email);
        this.loginTracker.next(true);
      }),
      catchError(err => {
        console.error('ユーザーの取得に失敗しました', err);
        this.logout();
        return of(null);
      })
    );
  }

  getPersistedToken(): string {
    return this.ss.getItem('token') || '';
  }

  logout() {
    [ 'userEmail', 'password','loggedIn', 'token'].forEach(item => this.ss.removeItem(item));
    this.loginTracker.next(false);
  }

  getAuthHeader() {
    return {
      headers: { 'Authorization': `Bearer ${this.getPersistedToken()}` }
    };
  };

  checkIfLoggedIn() {
    return this.ss.getItem('loggedIn') === 'true';
  }


}
