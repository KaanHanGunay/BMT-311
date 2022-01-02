import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { BookSearchComponent } from './book-search/book-search.component';
import { BookCreateComponent } from './book-create/book-create.component';
import { BookEditComponent } from './book-edit/book-edit.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'search',
    pathMatch: 'full',
  },
  {
    path: 'search',
    component: BookSearchComponent,
  },
  {
    path: 'create',
    component: BookCreateComponent,
  },
  {
    path: 'edit/:id',
    component: BookEditComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ComponentsRoutingModule {}
