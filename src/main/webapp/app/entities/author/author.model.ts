import * as dayjs from 'dayjs';
import { IBook } from 'app/entities/book/book.model';

export interface IAuthor {
  id?: number;
  name?: string | null;
  surname?: string | null;
  birthday?: dayjs.Dayjs | null;
  died?: dayjs.Dayjs | null;
  nationality?: string | null;
  books?: IBook[] | null;
}

export class Author implements IAuthor {
  constructor(
    public id?: number,
    public name?: string | null,
    public surname?: string | null,
    public birthday?: dayjs.Dayjs | null,
    public died?: dayjs.Dayjs | null,
    public nationality?: string | null,
    public books?: IBook[] | null
  ) {}
}

export function getAuthorIdentifier(author: IAuthor): number | undefined {
  return author.id;
}
