import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '../../config/authority.constants';
import { UserRouteAccessService } from '../../core/auth/user-route-access.service';
import { BooksMainComponent } from './books-main/books-main.component';
import { BooksSearchComponent } from './books-search/books-search.component';

const routes: Routes = [
  {
    path: '',
    component: BooksMainComponent,
    children: [
      {
        path: 'search',
        component: BooksSearchComponent,
        data: {
          authorities: [Authority.ADMIN, Authority.USER],
        },
        canActivate: [UserRouteAccessService],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BooksRoutingModule {}
