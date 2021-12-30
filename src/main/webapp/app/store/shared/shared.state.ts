import { HasId } from '../../shared/model/has-id.model';

export interface SharedState {
  showLoading: boolean;
  selectedRowEntity: HasId | null;
}

export const initialState: SharedState = {
  showLoading: false,
  selectedRowEntity: null,
};
