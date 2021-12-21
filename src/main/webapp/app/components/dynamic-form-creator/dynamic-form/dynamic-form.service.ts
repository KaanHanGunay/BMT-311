import { Injectable } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';
import { v4 } from 'uuid';

import { IDynamicFormCreatorModel, IDynamicFormFieldModel, ValueType } from '../models/dynamic-form-field.model';
import { DynamicReturnType } from '../models/dynamic-return-type.model';

@Injectable({
  providedIn: 'root',
})
export class DynamicFormService {
  private _entityId?: number;

  setEntityId(value: string | undefined): void {
    if (value) {
      this._entityId = Number(value);
    }
  }

  getEntityId(): number | undefined {
    return this._entityId;
  }

  findNumberOfMultipleFields(formCreator: IDynamicFormCreatorModel, field: IDynamicFormFieldModel): number {
    let count = 0;
    const id = this.findId(field);

    formCreator.fields.forEach(f => {
      if (f.id.includes(id)) {
        count++;
      }
    });

    return count;
  }

  findId(field: IDynamicFormFieldModel): string {
    return this.findIdFromString(field.id);
  }

  findIdFromString(fieldId: string): string {
    return fieldId.includes('_') ? fieldId.substring(0, fieldId.indexOf('_')) : fieldId;
  }

  getFormValue(formCreator: IDynamicFormCreatorModel, field: IDynamicFormFieldModel): DynamicReturnType {
    const control = field.multiple ? this.getMultiFieldsControls(formCreator, this.findId(field)) : [formCreator.form.get(field.id)];
    const values: any[] = [];

    control.forEach(c => {
      const result = (c as FormControl).value;
      if (result) {
        values.push(result);
      }
    });

    return field.multiple ? (values as DynamicReturnType) : (values[0] as DynamicReturnType);
  }

  getMultiFieldsControls(formCreator: IDynamicFormCreatorModel, fieldId: string): AbstractControl[] {
    const controls: AbstractControl[] = [];
    Object.keys(formCreator.form.controls).forEach(key => {
      if (key.startsWith(fieldId)) {
        controls.push(formCreator.form.get(key)!);
      }
    });

    return controls;
  }

  extractFieldArrays(formCreator: IDynamicFormCreatorModel): IDynamicFormCreatorModel {
    const fields: IDynamicFormFieldModel[] = [];
    formCreator.fields.forEach(field => {
      if (Array.isArray(field.value) && !field.listValue) {
        if (field.value.length === 0) {
          fields.push({ ...field, id: field.id + '_' + v4(), value: null });
        }

        field.value.forEach((val: ValueType) => {
          fields.push({ ...field, id: field.id + '_' + v4(), value: val });
        });
      } else {
        fields.push({ ...field });
      }
    });
    formCreator.fields = fields;
    return formCreator;
  }
}
