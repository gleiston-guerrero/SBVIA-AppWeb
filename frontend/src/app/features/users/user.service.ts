import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id?: number;
  firstName: string;
  lastName: string;
  username?: string;
  email: string;
  phone?: string;
  role: string;
  accountLocked: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private readonly API_URL = '/api/users';

  constructor(private http: HttpClient) { }

  list(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'userId,desc');
    return this.http.get(this.API_URL, { params });
  }

  changeRole(id: number, nombreRol: string): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/${id}/role`, { nombreRol });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  updateUser(id: number, data: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/${id}`, data);
  }
}
