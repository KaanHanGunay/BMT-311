import { RouterReducerState } from '@ngrx/router-store';

import { SHARED_STATE_NAME } from './shared/shared.selector';
import { SharedState } from './shared/shared.state';

export interface AppState {
  router: RouterReducerState;
  [SHARED_STATE_NAME]: SharedState;
}
