import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBorrowing } from '../borrowing.model';
import { BorrowingService } from '../service/borrowing.service';
import { BorrowingDeleteDialogComponent } from '../delete/borrowing-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-borrowing',
  templateUrl: './borrowing.component.html',
})
export class BorrowingComponent implements OnInit {
  borrowings?: IBorrowing[];
  isLoading = false;

  constructor(protected borrowingService: BorrowingService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.borrowingService.query().subscribe(
      (res: HttpResponse<IBorrowing[]>) => {
        this.isLoading = false;
        this.borrowings = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBorrowing): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(borrowing: IBorrowing): void {
    const modalRef = this.modalService.open(BorrowingDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.borrowing = borrowing;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
