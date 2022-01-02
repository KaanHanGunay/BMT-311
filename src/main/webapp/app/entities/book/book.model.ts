import { IAuthor } from 'app/entities/author/author.model';
import { IBorrowing } from 'app/entities/borrowing/borrowing.model';

export interface IBook {
  id?: number;
  title?: string;
  numOfPage?: number | null;
  coverUrl?: string | null;
  publisher?: string | null;
  author?: IAuthor | null;
  borrowings?: IBorrowing[] | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public title?: string,
    public numOfPage?: number | null,
    public coverUrl?: string | null,
    public publisher?: string | null,
    public author?: IAuthor | null,
    public borrowings?: IBorrowing[] | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
