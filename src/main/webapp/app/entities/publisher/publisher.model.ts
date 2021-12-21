import { IBook } from 'app/entities/book/book.model';

export interface IPublisher {
  id?: number;
  name?: string;
  books?: IBook[] | null;
}

export class Publisher implements IPublisher {
  constructor(public id?: number, public name?: string, public books?: IBook[] | null) {}
}

export function getPublisherIdentifier(publisher: IPublisher): number | undefined {
  return publisher.id;
}
