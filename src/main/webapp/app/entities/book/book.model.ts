import { IAuthor } from 'app/entities/author/author.model';
import { IPublisher } from 'app/entities/publisher/publisher.model';

export interface IBook {
  id?: number;
  title?: string;
  numOfPage?: number | null;
  coverContentType?: string | null;
  cover?: string | null;
  author?: IAuthor;
  publisher?: IPublisher | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public title?: string,
    public numOfPage?: number | null,
    public coverContentType?: string | null,
    public cover?: string | null,
    public author?: IAuthor,
    public publisher?: IPublisher | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
