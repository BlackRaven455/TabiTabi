import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowExplanationComponent } from './flow-explanation.component';

describe('FlowExplanationComponent', () => {
  let component: FlowExplanationComponent;
  let fixture: ComponentFixture<FlowExplanationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlowExplanationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlowExplanationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
