import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBorrowing, Borrowing } from '../borrowing.model';
import { BorrowingService } from '../service/borrowing.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';

@Component({
  selector: 'jhi-borrowing-update',
  templateUrl: './borrowing-update.component.html',
})
export class BorrowingUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  booksSharedCollection: IBook[] = [];

  editForm = this.fb.group({
    id: [],
    borrowingDate: [],
    deliveryDate: [],
    comment: [],
    user: [],
    book: [null, Validators.required],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected borrowingService: BorrowingService,
    protected userService: UserService,
    protected bookService: BookService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ borrowing }) => {
      this.updateForm(borrowing);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('kutuphanemApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const borrowing = this.createFromForm();
    if (borrowing.id !== undefined) {
      this.subscribeToSaveResponse(this.borrowingService.update(borrowing));
    } else {
      this.subscribeToSaveResponse(this.borrowingService.create(borrowing));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackBookById(index: number, item: IBook): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBorrowing>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(borrowing: IBorrowing): void {
    this.editForm.patchValue({
      id: borrowing.id,
      borrowingDate: borrowing.borrowingDate,
      deliveryDate: borrowing.deliveryDate,
      comment: borrowing.comment,
      user: borrowing.user,
      book: borrowing.book,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, borrowing.user);
    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing(this.booksSharedCollection, borrowing.book);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing(books, this.editForm.get('book')!.value)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));
  }

  protected createFromForm(): IBorrowing {
    return {
      ...new Borrowing(),
      id: this.editForm.get(['id'])!.value,
      borrowingDate: this.editForm.get(['borrowingDate'])!.value,
      deliveryDate: this.editForm.get(['deliveryDate'])!.value,
      comment: this.editForm.get(['comment'])!.value,
      user: this.editForm.get(['user'])!.value,
      book: this.editForm.get(['book'])!.value,
    };
  }
}
