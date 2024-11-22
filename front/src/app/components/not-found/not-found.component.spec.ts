import { ComponentFixture, TestBed } from '@angular/core/testing';
import * as j from '@jest/globals';
import { NotFoundComponent } from './not-found.component';
import { Router } from '@angular/router';

const router = {
  navigate: j.jest.fn(),
};

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotFoundComponent],
      providers: [{ provide: Router, useValue: router }],
    }).compileComponents();

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    router.navigate(['/unknown']);
    j.expect(component).toBeTruthy();
  });
});
