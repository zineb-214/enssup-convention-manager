import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { authDeletedGuard } from './auth-deleted.guard';

describe('authDeletedGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => authDeletedGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
