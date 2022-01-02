import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'jhi-borrow-dialog',
  templateUrl: './borrow-dialog.component.html',
  styleUrls: ['./borrow-dialog.component.scss'],
})
export class BorrowDialogComponent {
  constructor(public dialogRef: MatDialogRef<BorrowDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: string) {}
}
