import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';

import { BookSearch } from 'app/shared/models/book-search.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IBook } from 'app/entities/book/book.model';
import { RemoveDialogComponent } from '../remove-dialog/remove-dialog.component';
import { BorrowingService } from 'app/entities/borrowing/service/borrowing.service';
import { BorrowDialogComponent } from '../borrow-dialog/borrow-dialog.component';

@Component({
  selector: 'jhi-book-search',
  templateUrl: './book-search.component.html',
  styleUrls: ['./book-search.component.scss'],
})
export class BookSearchComponent implements OnInit, OnDestroy {
  searchForm!: FormGroup;
  searchResult?: IBook[];
  displayedColumns: string[] = ['title', 'authorName', 'authorSurname', 'actions'];
  dataSource?: MatTableDataSource<IBook>;
  removeSub?: Subscription;
  borrowSub?: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private _snackBar: MatSnackBar,
    private bookService: BookService,
    private router: Router,
    public dialog: MatDialog,
    private borrowService: BorrowingService
  ) {}

  ngOnInit(): void {
    this.searchForm = this.formBuilder.group({
      bookTitle: [null],
      authorName: [null],
      authorSurname: [null],
    });
  }

  ngOnDestroy(): void {
    this.removeSub?.unsubscribe();
  }

  search(): void {
    const searchParams: BookSearch = this.searchForm.value;

    if (searchParams.bookTitle === null && searchParams.authorName === null && searchParams.authorSurname === null) {
      this._snackBar.open('En az bir kriter girerek arama yapabilirsiniz', 'Kapat');
      return;
    }

    this.bookService.search(searchParams).subscribe(res => {
      if (res.body) {
        this.searchResult = res.body;
        this.dataSource = new MatTableDataSource<IBook>(this.searchResult);
      }
    });
  }

  navigateToUpdatePage(book: IBook): void {
    this.router.navigate(['/books', 'edit', book.id]);
  }

  navigateToBorrowPage(book: IBook): void {
    this.router.navigate(['/books', 'borrow', book.id]);
  }

  removeBook(book: IBook): void {
    const dialogRef = this.dialog.open(RemoveDialogComponent, {
      width: '250px',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.removeSub = this.bookService.delete(book.id!).subscribe(() => {
          this._snackBar.open('Kitap Başarıyla Silindi', 'Kapat', { duration: 2000 });
          this.searchResult = this.searchResult?.filter(b => b.id !== book.id);
          this.dataSource = new MatTableDataSource<IBook>(this.searchResult);
        });
      }
    });
  }

  borrowBook(book: IBook): void {
    const dialogRef = this.dialog.open(BorrowDialogComponent, {
      width: '250px',
      data: book.title,
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.borrowSub = this.borrowService.save(book.id!).subscribe(
          () => {
            this._snackBar.open('Ödünç alma başarılı bir şekilde eklendi.', 'Kapat', { duration: 2000 });
          },
          () => {
            this._snackBar.open('En fazla 3 kitap ödünç alınabilir.', 'Kapat', { duration: 2000 });
          }
        );
      }
    });
  }
}
