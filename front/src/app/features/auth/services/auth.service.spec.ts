import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import * as j from '@jest/globals';

describe('AuthService', () => {
  let authService: AuthService;
  let httpMock: HttpTestingController;

  const loginRequest: LoginRequest = {
    email: 'email@email.com',
    password: 'password',
  };

  const registerRequest: RegisterRequest = {
    email: 'email@email.com',
    password: 'password',
    firstName: 'firstname',
    lastName: 'lastname',
  };

  const mockSession: SessionInformation = {
    id: 1,
    token: 'token',
    type: 'type',
    username: 'email@email.com',
    firstName: 'firstname',
    lastName: 'lastname',
    admin: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
    authService = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    j.expect(authService).toBeTruthy();
  });

  describe('register', () => {
    it('should send a POST request for register', () => {
      authService.register(registerRequest).subscribe();

      const request = httpMock.expectOne('api/auth/register');
      j.expect(request.request.method).toBe('POST');
      j.expect(request.request.body).toEqual(registerRequest);
      request.flush(null);
    });
  });

  describe('login', () => {
    it('should send a POST request for login', () => {
      authService.login(loginRequest).subscribe((response) => {
        j.expect(response).toEqual(mockSession);
      });

      const request = httpMock.expectOne('api/auth/login');
      j.expect(request.request.method).toBe('POST');
      j.expect(request.request.body).toEqual(loginRequest);
      request.flush(mockSession);
    });
  });
});
