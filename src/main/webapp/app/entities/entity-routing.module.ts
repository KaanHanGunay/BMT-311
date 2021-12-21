import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'publisher',
        data: { pageTitle: 'kutuphanemApp.publisher.home.title' },
        loadChildren: () => import('./publisher/publisher.module').then(m => m.PublisherModule),
      },
      {
        path: 'author',
        data: { pageTitle: 'kutuphanemApp.author.home.title' },
        loadChildren: () => import('./author/author.module').then(m => m.AuthorModule),
      },
      {
        path: 'book',
        data: { pageTitle: 'kutuphanemApp.book.home.title' },
        loadChildren: () => import('./book/book.module').then(m => m.BookModule),
      },
      {
        path: 'borrowing',
        data: { pageTitle: 'kutuphanemApp.borrowing.home.title' },
        loadChildren: () => import('./borrowing/borrowing.module').then(m => m.BorrowingModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
