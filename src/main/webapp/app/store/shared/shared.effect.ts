import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { tap } from 'rxjs/operators';

import { ErrorSupportService } from '../../shared/services/error-support.service';
import { requestFailed } from './shared.action';

@Injectable({
  providedIn: 'root',
})
export class SharedEffect {
  requestFailed$ = createEffect(() =>
    this.action$.pipe(
      ofType(requestFailed),
      tap(action => this.errorService.errorHandler(action.error))
    )
  );

  constructor(private action$: Actions, private errorService: ErrorSupportService) {}
}
