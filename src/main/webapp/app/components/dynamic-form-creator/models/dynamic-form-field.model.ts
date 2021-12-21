import { FormControl, FormGroup, ValidatorFn } from '@angular/forms';
import * as dayjs from 'dayjs';

import { BooleanField } from './boolean-field';

export interface IDynamicFormCreatorModel {
  form: FormGroup;
  fields: IDynamicFormFieldModel[];
  addNewField: (field: IDynamicFormFieldModel) => void;
  removeField: (id: string) => void;
}

export class DynamicFormCreatorModel implements IDynamicFormCreatorModel {
  form: FormGroup;

  constructor(public fields: IDynamicFormFieldModel[]) {
    this.form = new FormGroup({});
  }

  addNewField(field: IDynamicFormFieldModel): void {
    this.form.addControl(field.id, new FormControl());
    this.fields.push(field);
  }

  removeField(id: string): void {
    this.form.removeControl('attendee');
    this.fields = this.fields.filter(fld => fld.id !== id);
  }
}

export interface DynamicFormFunctions {
  remove: () => void;
}

export interface IDynamicFormFieldModel {
  id: string;
  type: DynamicFormTypes;
  label: string;
  selectionType?: string;
  value?: ValueType | ValueArrayType;
  listValue?: boolean;
  readOnly?: boolean;
  multiple?: boolean;
  placeholder?: string;
  fieldsValues?: BooleanField;
  validators?: ValidatorFn[];
}

export class DynamicFormFieldModel implements IDynamicFormFieldModel {
  constructor(
    public id: string,
    public type: DynamicFormTypes,
    public label: string,
    public selectionType?: string,
    public value?: ValueType | ValueArrayType,
    public listValue?: boolean,
    public readOnly?: boolean,
    public multiple?: boolean,
    public placeholder?: string,
    public fieldsValues?: BooleanField,
    public validators?: ValidatorFn[]
  ) {}
}

export type ValueType = string | number | boolean | dayjs.Dayjs | null;

export type ValueArrayType = string[];

export type DynamicFormTypes = 'text' | 'number' | 'date' | 'multi-selections' | 'selections' | 'textarea';
