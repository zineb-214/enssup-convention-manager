import { TestBed } from '@angular/core/testing';

import { ConventionTypeService } from './convention-type.service';

describe('ConventionTypeService', () => {
  let service: ConventionTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConventionTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
