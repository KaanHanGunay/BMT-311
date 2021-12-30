import { HttpErrorResponse } from '@angular/common/http';
import { createAction, props } from '@ngrx/store';

export const EMPTY_ACTION = '[shared state] empty action';
export const SET_LOADING_SCREEN = '[shared state] set loading spinner';
export const REQUEST_FAILED = '[shared state] request failed';
export const SELECT_ROW = '[shared state] table select row';

export const emptyAction = createAction(EMPTY_ACTION);
export const setLoadingScreen = createAction(SET_LOADING_SCREEN, props<{ loading: boolean }>());
export const requestFailed = createAction(REQUEST_FAILED, props<{ error: HttpErrorResponse }>());
export const selectRow = createAction(SELECT_ROW, props<{ entity: any | null }>());
