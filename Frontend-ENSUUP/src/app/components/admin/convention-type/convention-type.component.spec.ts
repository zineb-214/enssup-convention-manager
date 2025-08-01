import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConventionTypeComponent } from './convention-type.component';

describe('ConventionTypeComponent', () => {
  let component: ConventionTypeComponent;
  let fixture: ComponentFixture<ConventionTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConventionTypeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConventionTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
