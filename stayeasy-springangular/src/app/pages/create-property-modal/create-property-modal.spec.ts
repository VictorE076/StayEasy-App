import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePropertyModal } from './create-property-modal';

describe('CreatePropertyModal', () => {
  let component: CreatePropertyModal;
  let fixture: ComponentFixture<CreatePropertyModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreatePropertyModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePropertyModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
