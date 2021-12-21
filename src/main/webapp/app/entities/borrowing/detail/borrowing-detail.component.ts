import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBorrowing } from '../borrowing.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-borrowing-detail',
  templateUrl: './borrowing-detail.component.html',
})
export class BorrowingDetailComponent implements OnInit {
  borrowing: IBorrowing | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ borrowing }) => {
      this.borrowing = borrowing;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
