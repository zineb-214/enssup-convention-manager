import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
export interface AppPermission {
  username: string;
  canCreate: boolean;
  canRead: boolean;
  canUpdate: boolean;
  canDelete: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface AppRole {
  id?: number;
  name: string;
}

export interface PermissionRequest {
  username: string;
  canCreate: boolean;
  canRead: boolean;
  canUpdate: boolean;
  canDelete: boolean;
}

export interface AppUser {
  id?: number;
  username: string;
  password?: string;
  email?: string;
  createdAt?: string;
  roles?: AppRole[];
  permissions: PermissionRequest[];
  deleted?: boolean;
}

export interface UserRole {
  username: string;
  roleName: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserServiceService {
  private baseUrl = 'http://localhost:8080/admin';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(`${this.baseUrl}/users`);
  }
  getRoles(): Observable<AppRole[]> {
    return this.http.get<AppRole[]>(`${this.baseUrl}/roles`);
  }

  getUserById(id: number): Observable<AppUser> {
    return this.http.get<AppUser>(`${this.baseUrl}/users/${id}`);
  }

  addUser(user: AppUser): Observable<AppUser> {
    return this.http.post<AppUser>(`${this.baseUrl}/addUser`, user);
  }

  updateUser(id: number, user: AppUser): Observable<AppUser> {
    return this.http.put<AppUser>(`${this.baseUrl}/updateUser/${id}`, user);
  }

  

 addPermissionsToUser(permission: PermissionRequest): Observable<string> {
  return this.http.post<string>(`${this.baseUrl}/addPermissionToUser`, permission, { responseType: 'text' as 'json' });
}



softDeleteUser(id: number): Observable<string> {
  return this.http.put<string>(`${this.baseUrl}/users/${id}/soft-delete`, {}, { responseType: 'text' as 'json' });
}
restoreUser(id: number): Observable<string> {
  return this.http.put<string>(`${this.baseUrl}/users/${id}/restore`, {}, { responseType: 'text' as 'json' });
}

filterUsers(
  username?: string,
  isDeleted?: boolean,
  startDate?: string,
  endDate?: string,
  page: number = 0,
  size: number = 2,
  sort: string = 'createdAt,desc'
): Observable<Page<AppUser>> {
  let params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sort', sort);

  if (username) params = params.set('username', username);
  if (isDeleted !== null && isDeleted !== undefined) params = params.set('isDeleted', isDeleted.toString());
  if (startDate) params = params.set('startDate', startDate);
  if (endDate) params = params.set('endDate', endDate);

  return this.http.get<Page<AppUser>>(`${this.baseUrl}/filterUsers`, { params });
}


}
