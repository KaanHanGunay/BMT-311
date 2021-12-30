import { routerReducer } from '@ngrx/router-store';

import { SHARED_STATE_NAME } from './shared/shared.selector';
import { sharedReducer } from './shared/shared.reducer';

export const appReducer = {
  router: routerReducer,
  [SHARED_STATE_NAME]: sharedReducer,
};
