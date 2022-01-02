import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { SharedModule } from 'app/shared/shared.module';
import { BorrowingRoutingModule } from './borrowing-routing.module';
import { BorrowingListComponent } from './borrowing-list/borrowing-list.component';
import { BorrowingDeliverComponent } from './borrowing-deliver/borrowing-deliver.component';

@NgModule({
  declarations: [BorrowingListComponent, BorrowingDeliverComponent],
  imports: [CommonModule, BorrowingRoutingModule, SharedModule, ReactiveFormsModule],
})
export class BorrowingModule {}
