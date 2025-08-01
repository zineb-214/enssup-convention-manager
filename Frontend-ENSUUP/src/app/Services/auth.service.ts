import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { BehaviorSubject, delay, filter, Observable, of, tap, throwError } from 'rxjs';
import { AppUser } from './user-service.service';
export interface PermissionRequest {
  canCreate: boolean;
  canRead: boolean;
  canUpdate: boolean;
  canDelete: boolean;
  username: string;
}
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  isauthenticated: boolean = false;
  username: string | null = null;
accessToen: string | null = null;
  roles: string[] = [];
  private permissionsSubject = new BehaviorSubject<PermissionRequest | null>(null);

  constructor(private http: HttpClient) {}
  public login(username: string, password: string) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
    });

    const params = new HttpParams()
      .set('username', username)
      .set('password', password);

    return this.http
      .post<{ access_token: string }>(
        'http://localhost:8080/auth/login',
        params.toString(),
        { headers }
      )
      .pipe(
        tap((response) => {
          if (response.access_token) {
            localStorage.setItem('token', response.access_token); 
          }
        })
      );
  }


public profile(data: any) {
  const token = data?.['access_token'];

  if (!token || typeof token !== 'string') {
    this.isauthenticated = false;
    this.accessToen = null;
    this.roles = [];
    return;
  }

  this.isauthenticated = true;
  this.accessToen = token;
  localStorage.setItem('token', token);

  try {
    const jwtdecode: any = jwtDecode(token);
    this.username = jwtdecode.sub || null;
    this.roles = jwtdecode.roles || [];

    const perms = jwtdecode.permissions;
    if (perms) {
      const extractedPermissions: PermissionRequest = {
        username: jwtdecode.sub,
        canCreate: perms.canCreate,
        canRead: perms.canRead,
        canUpdate: perms.canUpdate,
        canDelete: perms.canDelete,
      };
      this.setUserPermissions(extractedPermissions);
    } else {
      this.setUserPermissions({
        username: jwtdecode.sub,
        canCreate: false,
        canRead: false,
        canUpdate: false,
        canDelete: false,
      });
    }
  } catch (e) {
    this.isauthenticated = false;
    this.roles = [];
    this.accessToen = null;
    localStorage.removeItem('token');
  }
}


  // ✅ FOURNIR LES PERMISSIONS DEPUIS LE TOKEN
  loadPermissions(): Observable<PermissionRequest> {
    const decoded: any = this.decodeToken();
    if (decoded && decoded.permissions) {
      const permissions: PermissionRequest = {
        username: decoded.sub,
        canCreate: decoded.permissions.canCreate,
        canRead: decoded.permissions.canRead,
        canUpdate: decoded.permissions.canUpdate,
        canDelete: decoded.permissions.canDelete,
      };
      this.setUserPermissions(permissions);
      return of(permissions);
    } else {
    return throwError(() => new Error('Permissions not found in token'));

    }
  }

  

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  decodeToken(): any {
    const token = this.getToken();
    if (token) {
      try {
        return jwtDecode(token);
      } catch (e) {
        return null;
      }
    }
    return null;
  }

  getUserRoles(): string[] {
    return this.roles;
  }

  getUserIdFromToken(): number | null {
    const decoded: any = this.decodeToken();
    return decoded?.userId || decoded?.id || +decoded?.sub || null;
  }

  isTokenExpired(token: string): boolean {
    try {
      const decoded: any = jwtDecode(token);
      const now = Math.floor(Date.now() / 1000);
      return decoded.exp < now;
    } catch (e) {
      return true;
    }
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return token !== null && !this.isTokenExpired(token);
  }

logout(): void {
  this.isauthenticated = false;
  this.username = null;
  this.accessToen = null;
  this.roles = [];
  this.permissionsSubject.next(null);
  localStorage.removeItem('token');
}

  setUserPermissions(permissions: PermissionRequest) {
    this.permissionsSubject.next(permissions);
  }

  getUserPermissions(): PermissionRequest | null {
    return this.permissionsSubject.value;
  }

  getUserPermissionsAsync(): Observable<PermissionRequest> {
    if (!this.permissionsSubject.value) {
      this.http.get<PermissionRequest>('/api/auth/permissions')
        .subscribe(permissions => this.permissionsSubject.next(permissions));
    }
    return this.permissionsSubject.asObservable().pipe(filter(p => p !== null));
  }

  get permissions(): PermissionRequest | null {
    return this.permissionsSubject.value;
  }

  getCurrentUser(): Observable<AppUser> {
  return this.http.get<AppUser>('http://localhost:8080/me');
}

}