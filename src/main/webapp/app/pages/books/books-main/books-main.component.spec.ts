import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BooksMainComponent } from './books-main.component';

describe('BooksMainComponent', () => {
  let component: BooksMainComponent;
  let fixture: ComponentFixture<BooksMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BooksMainComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BooksMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
