import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormComponent } from './form.component';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionApiService } from '../../services/session-api.service';
import * as j from '@jest/globals';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiServiceMock: any;
  let sessionServiceMock: any;
  let teacherServiceMock: any;
  let matSnackBarMock: any;
  let routerMock: any;
  let activatedRouteMock: any;

  beforeEach(async () => {
    sessionApiServiceMock = {
      detail: j.jest.fn().mockReturnValue(of({})),
      create: j.jest.fn().mockReturnValue(of({})),
      update: j.jest.fn().mockReturnValue(of({})),
      all: j.jest.fn().mockReturnValue(of([])),
    };

    sessionServiceMock = {
      sessionInformation: { admin: true, name: 'Admin User' },
      $isLogged: j.jest.fn(),
      logIn: j.jest.fn(),
      logOut: j.jest.fn(),
    };

    teacherServiceMock = {
      all: j.jest.fn().mockReturnValue(of([])),
    };

    matSnackBarMock = {
      open: j.jest.fn(),
    };

    routerMock = {
      navigate: j.jest.fn(),
      url: '/sessions/create',
    } as any;

    activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: j.jest.fn().mockReturnValue('1'),
        },
      },
    } as any;

    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    j.expect(component).toBeTruthy();
  });

  it('should initialize the form for creating a new session', () => {
    component.ngOnInit();

    j.expect(component.sessionForm).toBeTruthy();
    j.expect(component.sessionForm?.value).toEqual({
      name: '',
      date: '',
      teacher_id: '',
      description: '',
    });
  });

  it('should initialize the form for updating a session', () => {
    const mockSession = {
      id: '1',
      name: 'Yoga Session',
      date: '2024-11-25',
      teacher_id: '123',
      description: 'A relaxing yoga session',
    };

    sessionApiServiceMock
      .detail(mockSession.id)
      .subscribe((response: Session) => {
        j.expect(response).toEqual(mockSession);
      });
  });

  it('should call create API when submitting a new session', () => {
    const mockSession = {
      name: 'New Session',
      date: '2024-11-30',
      teacher_id: '123',
      description: 'Description of new session',
    };

    component.sessionForm?.setValue(mockSession);
    component.onUpdate = false;

    sessionApiServiceMock.create.mockReturnValue(of(mockSession));
    component.submit();

    j.expect(sessionApiServiceMock.create).toBeCalledWith(mockSession);
    j.expect(matSnackBarMock.open).toBeCalledWith(
      'Session created !',
      'Close',
      { duration: 3000 },
    );
    j.expect(routerMock.navigate).toBeCalledWith(['sessions']);
  });

  it('should call update API when submitting an updated session', () => {
    const mockSession = {
      name: 'Updated Session',
      date: '2024-11-30',
      teacher_id: '123',
      description: 'Updated description of session',
    };

    component.sessionForm?.setValue(mockSession);
    component.onUpdate = true;

    sessionApiServiceMock.update.mockReturnValue(of(mockSession));
    component.submit();

    sessionApiServiceMock
      .update(1, mockSession)
      .subscribe((response: Session) => {
        j.expect(response).toEqual(mockSession);
      });

    j.expect(matSnackBarMock.open).toBeCalledWith(
      'Session updated !',
      'Close',
      { duration: 3000 },
    );
    j.expect(routerMock.navigate).toBeCalledWith(['sessions']);
  });

  it('should navigate away if user is not an admin', () => {
    sessionServiceMock.sessionInformation = {
      admin: false,
      name: 'Regular User',
    };

    component.ngOnInit();

    j.expect(routerMock.navigate).toBeCalledWith(['/sessions']);
  });
});
