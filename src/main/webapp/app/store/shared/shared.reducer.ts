import { Action, createReducer, on } from '@ngrx/store';

import { initialState } from './shared.state';
import { selectRow, setLoadingScreen } from './shared.action';

const _sharedReducer = createReducer(
  initialState,
  on(setLoadingScreen, (state, action) => ({ ...state, showLoading: action.loading })),
  on(selectRow, (state, action) => ({ ...state, selectedRowEntity: action.entity }))
);

export function sharedReducer(state: any, action: Action): any {
  return _sharedReducer(state, action);
}
