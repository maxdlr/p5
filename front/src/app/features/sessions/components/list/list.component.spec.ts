import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListComponent } from './list.component';
import { SessionService } from '../../../../services/session.service';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { SessionApiService } from '../../services/session-api.service';
import * as j from '@jest/globals';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiServiceMock: any;
  let sessionServiceMock: any;

  beforeEach(async () => {
    sessionApiServiceMock = {
      all: j.jest.fn(),
    };

    sessionServiceMock = {
      $isLogged: j.jest.fn(),
      logIn: j.jest.fn(),
      logOut: j.jest.fn(),
      sessionInformation: { admin: true, name: 'John Doe' },
    };

    const mockSessions = [
      {
        id: '1',
        name: 'Yoga Class',
        date: '2024-11-25',
        description: 'A relaxing yoga session',
      },
      {
        id: '2',
        name: 'Pilates',
        date: '2024-11-30',
        description: 'A challenging Pilates class',
      },
    ];

    sessionApiServiceMock.all.mockReturnValue(of(mockSessions));

    sessionServiceMock.sessionInformation = { admin: true, name: 'John Doe' };

    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    j.expect(component).toBeTruthy();
  });

  it('should fetch sessions and display them', () => {
    const sessionCards = fixture.debugElement.queryAll(By.css('.item'));
    j.expect(sessionCards.length).toBe(2);
  });
});
