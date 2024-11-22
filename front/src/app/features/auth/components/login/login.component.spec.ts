import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import * as j from '@jest/globals';

import { LoginComponent } from './login.component';
import { SessionService } from '../../../../services/session.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const mockAuthService = {
    login: j.jest.fn(),
  };

  const mockSessionService = {
    logIn: j.jest.fn(),
  };

  const mockRouter = {
    navigate: j.jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        FormBuilder,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    j.expect(component).toBeTruthy();
  });

  it('should disable submit button when form is invalid', () => {
    component.form.controls['email'].setValue('');
    component.form.controls['password'].setValue('');
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button[type="submit"]');
    j.expect(button.disabled).toBe(true);
  });

  it('should enable submit button when form is valid', () => {
    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('password');
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button[type="submit"]');
    j.expect(button.disabled).toBe(false);
  });

  it('should call authService.login and navigate on successful login', () => {
    const loginResponse = { id: 1, admin: true };
    mockAuthService.login.mockReturnValue(of(loginResponse));

    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('password');
    component.submit();

    j.expect(mockAuthService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password',
    });
    j.expect(mockSessionService.logIn).toHaveBeenCalledWith(loginResponse);
    j.expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should set onError to true on login failure', () => {
    mockAuthService.login.mockReturnValue(
      throwError(() => new Error('Login failed')),
    );

    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('password');
    component.submit();

    j.expect(mockAuthService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password',
    });
    j.expect(component.onError).toBe(true);
  });

  it('should display error message when onError is true', () => {
    component.onError = true;
    fixture.detectChanges();

    const errorMessage = fixture.nativeElement.querySelector('.error');
    j.expect(errorMessage).toBeTruthy();
    j.expect(errorMessage.textContent).toContain('An error occurred');
  });
});
