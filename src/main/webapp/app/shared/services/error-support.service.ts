import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ErrorSupportService {
  constructor(private toastr: ToastrService) {}

  errorHandler(err: HttpErrorResponse): void {
    switch (err.status) {
      case 401:
        this.toastr.error('Yetkisiz İşlem');
        break;
      case 406:
        this.toastr.error(err.error['detail'], err.error['title']);
        break;
      default:
        this.toastr.error('Sorgulama esnasında hata meydana geldi', 'Hata');
        break;
    }
  }
}
