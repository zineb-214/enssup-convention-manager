import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { AuthInterceptor } from './auth-http.interceptor';

describe('AuthInterceptor (class)', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: HTTP_INTERCEPTORS,
          useClass: AuthInterceptor,
          multi: true
        }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // s'assurer qu’aucune requête en attente
  });

  it('should add Authorization header when token exists', () => {
    localStorage.setItem('token', 'FAKE_TOKEN');

    http.get('/secured').subscribe();

    const req = httpMock.expectOne('/secured');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe('Bearer FAKE_TOKEN');
  });

  it('should not add Authorization header when token does not exist', () => {
    localStorage.removeItem('token');

    http.get('/public').subscribe();

    const req = httpMock.expectOne('/public');
    expect(req.request.headers.has('Authorization')).toBeFalse();
  });
});
