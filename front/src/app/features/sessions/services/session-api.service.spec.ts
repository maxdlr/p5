import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { SessionApiService } from './session-api.service';
import { of } from 'rxjs';
import * as j from '@jest/globals';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpClientMock: any;

  let mockSession = {
    id: 1,
    name: 'Yoga Session',
    date: new Date(),
    teacher_id: 2,
    description: 'A relaxing yoga session',
    users: [1, 2, 3],
  };

  beforeEach(() => {
    httpClientMock = {
      get: j.jest.fn(),
      post: j.jest.fn(),
      put: j.jest.fn(),
      delete: j.jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        SessionApiService,
        { provide: HttpClient, useValue: httpClientMock },
      ],
    });

    service = TestBed.inject(SessionApiService);
  });

  it('should be created', () => {
    j.expect(service).toBeTruthy();
  });

  it('should call HttpClient.get with the correct URL for all()', () => {
    httpClientMock.get.mockReturnValue(of(mockSession));

    service.all().subscribe((response) => {
      j.expect(response).toEqual(mockSession);
    });

    j.expect(httpClientMock.get).toBeCalledWith('api/session');
  });

  it('should call HttpClient.get with the correct URL for detail()', () => {
    const sessionId = '123';
    httpClientMock.get.mockReturnValue(of(mockSession));

    service.detail(sessionId).subscribe((response) => {
      j.expect(response).toEqual(mockSession);
    });

    j.expect(httpClientMock.get).toBeCalledWith('api/session/123');
  });

  it('should call HttpClient.delete with the correct URL for delete()', () => {
    const sessionId = '123';
    httpClientMock.delete.mockReturnValue(of(null));

    service.delete(sessionId).subscribe((response) => {
      j.expect(response).toBeNull();
    });

    j.expect(httpClientMock.delete).toBeCalledWith('api/session/123');
  });

  it('should call HttpClient.post with the correct URL and body for create()', () => {
    httpClientMock.post.mockReturnValue(of(mockSession));

    service.create(mockSession).subscribe((response) => {
      j.expect(response).toEqual(mockSession);
    });

    j.expect(httpClientMock.post).toBeCalledWith('api/session', mockSession);
  });

  it('should call HttpClient.put with the correct URL and body for update()', () => {
    const sessionId = '123';
    httpClientMock.put.mockReturnValue(of(mockSession));

    service.update(sessionId, mockSession).subscribe((response) => {
      j.expect(response).toEqual(mockSession);
    });

    j.expect(httpClientMock.put).toBeCalledWith('api/session/123', mockSession);
  });

  it('should call HttpClient.post with the correct URL for participate()', () => {
    const sessionId = '123';
    const userId = '456';
    httpClientMock.post.mockReturnValue(of(null));

    service.participate(sessionId, userId).subscribe((response) => {
      j.expect(response).toBeNull();
    });

    j.expect(httpClientMock.post).toBeCalledWith(
      'api/session/123/participate/456',
      null,
    );
  });

  it('should call HttpClient.delete with the correct URL for unParticipate()', () => {
    const sessionId = '123';
    const userId = '456';
    httpClientMock.delete.mockReturnValue(of(null));

    service.unParticipate(sessionId, userId).subscribe((response) => {
      j.expect(response).toBeNull();
    });

    j.expect(httpClientMock.delete).toBeCalledWith(
      'api/session/123/participate/456',
    );
  });
});
