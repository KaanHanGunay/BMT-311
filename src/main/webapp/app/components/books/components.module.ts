import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { SharedModule } from '../../shared/shared.module';
import { BookSearchComponent } from './book-search/book-search.component';
import { ComponentsRoutingModule } from './components-routing.module';
import { BookCreateComponent } from './book-create/book-create.component';
import { BookEditComponent } from './book-edit/book-edit.component';
import { RemoveDialogComponent } from './remove-dialog/remove-dialog.component';
import { BorrowDialogComponent } from './borrow-dialog/borrow-dialog.component';

@NgModule({
  declarations: [BookSearchComponent, BookCreateComponent, BookEditComponent, RemoveDialogComponent, BorrowDialogComponent],
  imports: [CommonModule, SharedModule, ReactiveFormsModule, ComponentsRoutingModule],
})
export class ComponentsModule {}
