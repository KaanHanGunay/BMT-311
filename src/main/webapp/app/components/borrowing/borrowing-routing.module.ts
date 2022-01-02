import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { BorrowingListComponent } from './borrowing-list/borrowing-list.component';
import { BorrowingDeliverComponent } from './borrowing-deliver/borrowing-deliver.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'list',
    pathMatch: 'full',
  },
  {
    path: 'list',
    component: BorrowingListComponent,
  },
  {
    path: 'deliver/:id',
    component: BorrowingDeliverComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BorrowingRoutingModule {}
