import { createFeatureSelector, createSelector } from '@ngrx/store';

import { RouterStateUrl } from './custom-serializer';
import { RouterReducerState } from '@ngrx/router-store';

export const getRouterState = createFeatureSelector<RouterReducerState<RouterStateUrl>>('router');

export const getCurrentRouter = createSelector(getRouterState, state => state.state);
