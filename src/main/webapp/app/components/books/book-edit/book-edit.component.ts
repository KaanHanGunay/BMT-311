import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';

import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';

@Component({
  selector: 'jhi-book-edit',
  templateUrl: './book-edit.component.html',
  styleUrls: ['./book-edit.component.scss'],
})
export class BookEditComponent implements OnInit, OnDestroy {
  book!: IBook;
  updateForm?: FormGroup;
  authorSub!: Subscription;
  authors!: IAuthor[];
  bookSub!: Subscription;
  updateBookSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private router: Router,
    private formBuilder: FormBuilder,
    private authorService: AuthorService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];

    if (!id) {
      window.history.back();
    }

    this.authorSub = this.authorService.query().subscribe(res => {
      if (res.body) {
        this.authors = res.body;
        this.bookSub = this.bookService.find(id).subscribe(resp => {
          if (resp.body) {
            this.book = resp.body;
            this.updateForm = this.formBuilder.group({
              title: [this.book.title, Validators.required],
              numOfPage: [this.book.numOfPage],
              coverUrl: [this.book.coverUrl],
              publisher: [this.book.publisher],
              author: [this.book.author, Validators.required],
            });
          } else {
            this.router.navigate(['404']);
          }
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.bookSub.unsubscribe();
    this.authorSub.unsubscribe();
  }

  updateBook(): void {
    const updatedBook: IBook = { ...this.updateForm!.value, id: this.book.id };
    this.updateBookSub = this.bookService.update(updatedBook).subscribe(() => {
      this._snackBar.open('Kitap Başarıyla Güncellendi.', 'Kapat');
      window.history.back();
    });
  }

  compareCategoryObjects(object1: any, object2: any): boolean {
    return !!object1 && !!object2 && object1.id === object2.id;
  }
}
