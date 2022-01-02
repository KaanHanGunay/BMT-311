import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';
import { IBook } from 'app/entities/book/book.model';

export interface IBorrowing {
  id?: number;
  borrowingDate?: dayjs.Dayjs | null;
  deliveryDate?: dayjs.Dayjs | null;
  comment?: string | null;
  user?: IUser | null;
  book?: IBook | null;
}

export class Borrowing implements IBorrowing {
  constructor(
    public id?: number,
    public borrowingDate?: dayjs.Dayjs | null,
    public deliveryDate?: dayjs.Dayjs | null,
    public comment?: string | null,
    public user?: IUser | null,
    public book?: IBook | null
  ) {}
}

export function getBorrowingIdentifier(borrowing: IBorrowing): number | undefined {
  return borrowing.id;
}
