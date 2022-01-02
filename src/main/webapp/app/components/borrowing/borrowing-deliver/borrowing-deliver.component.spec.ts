import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BorrowingDeliverComponent } from './borrowing-deliver.component';

describe('BorrowingDeliverComponent', () => {
  let component: BorrowingDeliverComponent;
  let fixture: ComponentFixture<BorrowingDeliverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BorrowingDeliverComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BorrowingDeliverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
