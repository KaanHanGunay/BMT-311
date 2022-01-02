import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';

import { BookService } from 'app/entities/book/service/book.service';
import { IBook } from 'app/entities/book/book.model';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';

@Component({
  selector: 'jhi-book-create',
  templateUrl: './book-create.component.html',
  styleUrls: ['./book-create.component.scss'],
})
export class BookCreateComponent implements OnInit, OnDestroy {
  createForm!: FormGroup;
  newBook?: IBook;
  creationSub?: Subscription;
  authorSub!: Subscription;
  authors!: IAuthor[];

  constructor(
    private formBuilder: FormBuilder,
    private _snackBar: MatSnackBar,
    private bookService: BookService,
    private authorService: AuthorService
  ) {}

  ngOnInit(): void {
    this.createForm = this.formBuilder.group({
      title: [null, Validators.required],
      numOfPage: [null],
      coverUrl: [null],
      publisher: [null],
      author: [null, Validators.required],
    });

    this.authorSub = this.authorService.query().subscribe(res => {
      if (res.body) {
        this.authors = res.body;
      }
    });
  }

  ngOnDestroy(): void {
    this.creationSub?.unsubscribe();
    this.authorSub.unsubscribe();
  }

  createBook(): void {
    this.newBook = this.createForm.value;
    if (this.newBook) {
      this.creationSub = this.bookService.create(this.newBook).subscribe(() => {
        this._snackBar.open('Yeni kitap başarıyla eklendi.', 'Kapat');
        this.createForm.reset();
      });
    }
  }
}
