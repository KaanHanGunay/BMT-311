import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import * as dayjs from 'dayjs';

import { IBorrowing } from 'app/entities/borrowing/borrowing.model';
import { BorrowingService } from 'app/entities/borrowing/service/borrowing.service';

@Component({
  selector: 'jhi-borrowing-deliver',
  templateUrl: './borrowing-deliver.component.html',
  styleUrls: ['./borrowing-deliver.component.scss'],
})
export class BorrowingDeliverComponent implements OnInit, OnDestroy {
  borrow!: IBorrowing;
  borrowSub!: Subscription;
  borrowForm!: FormGroup;
  borrowUpdateSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private borrowService: BorrowingService,
    private formBuilder: FormBuilder,
    private _snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];

    if (!id) {
      window.history.back();
    }

    this.borrowSub = this.borrowService.find(id).subscribe(res => {
      if (res.body) {
        this.borrow = res.body;
        this.borrowForm = this.formBuilder.group({
          book: [{ value: this.borrow.book?.title, disabled: true }],
          borrowingDate: [{ value: this.borrow.borrowingDate?.format('DD.MM.YYYY'), disabled: true }],
          comment: [null],
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.borrowSub.unsubscribe();
    this.borrowUpdateSub?.unsubscribe();
  }

  deliver(): void {
    this.borrow.comment = this.borrowForm.get('comment')?.value;
    this.borrow.deliveryDate = dayjs();

    this.borrowUpdateSub = this.borrowService.update(this.borrow).subscribe(() => {
      this._snack.open('Kitap teslim edildi.', 'Kapat', { duration: 2000 });
      window.history.back();
    });
  }
}
