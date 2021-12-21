import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { BorrowingService } from '../service/borrowing.service';

import { BorrowingComponent } from './borrowing.component';

describe('Borrowing Management Component', () => {
  let comp: BorrowingComponent;
  let fixture: ComponentFixture<BorrowingComponent>;
  let service: BorrowingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [BorrowingComponent],
    })
      .overrideTemplate(BorrowingComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BorrowingComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BorrowingService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.borrowings?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
