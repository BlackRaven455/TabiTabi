import { TestBed } from '@angular/core/testing';

import { OpentripService } from './opentrip.service';

describe('OpentripService', () => {
  let service: OpentripService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OpentripService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
