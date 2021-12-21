import { InjectionToken } from '@angular/core';

import { DynamicFormFunctions } from '../models/dynamic-form-field.model';

export const DYNAMIC_FIELD = new InjectionToken<DynamicFormFunctions>('DynamicField');
