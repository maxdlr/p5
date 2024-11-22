import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import * as j from '@jest/globals';

import { MeComponent } from './me.component';
import { SessionService } from '../../services/session.service';
import { UserService } from '../../services/user.service';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: { id: 1, admin: false },
    isLogged: true,
    logOut: j.jest.fn(),
  };

  const mockUserService = {
    getById: j.jest.fn().mockReturnValue(of({ id: 1, name: 'John Doe' })),
    delete: j.jest.fn().mockReturnValue(of({})),
  };

  const mockRouter = {
    navigate: j.jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [MatSnackBarModule, HttpClientModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create and call ngOnInit', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    j.expect(mockUserService.getById).toBeCalledWith('1');
    j.expect(component).toBeTruthy();
    j.expect(component.user).toEqual({ id: 1, name: 'John Doe' });
  });

  it('should navigate back when back() is called', () => {
    const historyBackSpy = j.jest.spyOn(window.history, 'back');
    component.back();

    j.expect(historyBackSpy).toBeCalled();
  });

  it('should delete the user', async () => {
    component.delete();
    await fixture.whenStable();

    j.expect(mockUserService.delete).toBeCalledWith('1');
  });

  it('should show a snackbar message when delete() is called', () => {
    const snackBarSpy = j.jest.spyOn(component['matSnackBar'], 'open');
    component.delete();

    j.expect(snackBarSpy).toBeCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 },
    );
  });
});
