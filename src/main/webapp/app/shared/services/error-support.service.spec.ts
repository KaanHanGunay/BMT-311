import { TestBed } from '@angular/core/testing';

import { ErrorSupportService } from './error-support.service';

describe('ErrorSupportService', () => {
  let service: ErrorSupportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ErrorSupportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
