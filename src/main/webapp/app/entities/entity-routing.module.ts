import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'author',
        data: { pageTitle: 'kutuphaneApp.author.home.title' },
        loadChildren: () => import('./author/author.module').then(m => m.AuthorModule),
      },
      {
        path: 'book',
        data: { pageTitle: 'kutuphaneApp.book.home.title' },
        loadChildren: () => import('./book/book.module').then(m => m.BookModule),
      },
      {
        path: 'borrowing',
        data: { pageTitle: 'kutuphaneApp.borrowing.home.title' },
        loadChildren: () => import('./borrowing/borrowing.module').then(m => m.BorrowingModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
