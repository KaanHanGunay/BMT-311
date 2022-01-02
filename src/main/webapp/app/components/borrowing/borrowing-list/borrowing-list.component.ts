import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';

import { IBorrowing } from 'app/entities/borrowing/borrowing.model';
import { BorrowingService } from 'app/entities/borrowing/service/borrowing.service';

@Component({
  selector: 'jhi-borrowing-list',
  templateUrl: './borrowing-list.component.html',
  styleUrls: ['./borrowing-list.component.scss'],
})
export class BorrowingListComponent implements OnInit, OnDestroy {
  borrows!: IBorrowing[];
  borrowSub!: Subscription;
  displayedColumns: string[] = ['book', 'borrowingDate', 'deliveryDate', 'comment', 'actions'];
  dataSource?: MatTableDataSource<IBorrowing>;

  constructor(private borrowService: BorrowingService, private router: Router) {}

  ngOnInit(): void {
    this.borrowSub = this.borrowService.getMyBorrowings().subscribe(res => {
      if (res.body) {
        this.borrows = res.body;
        this.dataSource = new MatTableDataSource<IBorrowing>(this.borrows);
      }
    });
  }

  ngOnDestroy(): void {
    this.borrowSub.unsubscribe();
  }

  redirectToDeliverPage(borrowing: IBorrowing): void {
    this.router.navigate(['/borrow', 'deliver', borrowing.id]);
  }
}
