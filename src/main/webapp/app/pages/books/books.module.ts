import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BooksRoutingModule } from './books-routing.module';
import { BooksMainComponent } from './books-main/books-main.component';
import { BooksSearchComponent } from './books-search/books-search.component';

@NgModule({
  declarations: [BooksMainComponent, BooksSearchComponent],
  imports: [CommonModule, BooksRoutingModule],
})
export class BooksModule {}
