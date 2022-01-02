import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBorrowing, getBorrowingIdentifier } from '../borrowing.model';

export type EntityResponseType = HttpResponse<IBorrowing>;
export type EntityArrayResponseType = HttpResponse<IBorrowing[]>;

@Injectable({ providedIn: 'root' })
export class BorrowingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/borrowings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(borrowing: IBorrowing): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(borrowing);
    return this.http
      .post<IBorrowing>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  save(bookId: number): Observable<EntityResponseType> {
    return this.http
      .post<IBorrowing>(`${this.resourceUrl}/book=${bookId}`, bookId, { observe: 'response' })
      .pipe(map(res => this.convertDateFromServer(res)));
  }

  update(borrowing: IBorrowing): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(borrowing);
    return this.http
      .put<IBorrowing>(`${this.resourceUrl}/${getBorrowingIdentifier(borrowing) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(borrowing: IBorrowing): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(borrowing);
    return this.http
      .patch<IBorrowing>(`${this.resourceUrl}/${getBorrowingIdentifier(borrowing) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBorrowing>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBorrowing[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  getMyBorrowings(): Observable<EntityArrayResponseType> {
    return this.http
      .get<IBorrowing[]>(`${this.resourceUrl}/mine`, { observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBorrowingToCollectionIfMissing(
    borrowingCollection: IBorrowing[],
    ...borrowingsToCheck: (IBorrowing | null | undefined)[]
  ): IBorrowing[] {
    const borrowings: IBorrowing[] = borrowingsToCheck.filter(isPresent);
    if (borrowings.length > 0) {
      const borrowingCollectionIdentifiers = borrowingCollection.map(borrowingItem => getBorrowingIdentifier(borrowingItem)!);
      const borrowingsToAdd = borrowings.filter(borrowingItem => {
        const borrowingIdentifier = getBorrowingIdentifier(borrowingItem);
        if (borrowingIdentifier == null || borrowingCollectionIdentifiers.includes(borrowingIdentifier)) {
          return false;
        }
        borrowingCollectionIdentifiers.push(borrowingIdentifier);
        return true;
      });
      return [...borrowingsToAdd, ...borrowingCollection];
    }
    return borrowingCollection;
  }

  protected convertDateFromClient(borrowing: IBorrowing): IBorrowing {
    return Object.assign({}, borrowing, {
      borrowingDate: borrowing.borrowingDate?.isValid() ? borrowing.borrowingDate.format(DATE_FORMAT) : undefined,
      deliveryDate: borrowing.deliveryDate?.isValid() ? borrowing.deliveryDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.borrowingDate = res.body.borrowingDate ? dayjs(res.body.borrowingDate) : undefined;
      res.body.deliveryDate = res.body.deliveryDate ? dayjs(res.body.deliveryDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((borrowing: IBorrowing) => {
        borrowing.borrowingDate = borrowing.borrowingDate ? dayjs(borrowing.borrowingDate) : undefined;
        borrowing.deliveryDate = borrowing.deliveryDate ? dayjs(borrowing.deliveryDate) : undefined;
      });
    }
    return res;
  }
}
