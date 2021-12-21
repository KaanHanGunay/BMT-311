import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';

import { DYNAMIC_FIELD } from '../token/dyanmic-form.token';
import { DynamicFormFunctions, IDynamicFormFieldModel } from '../models/dynamic-form-field.model';

@Component({
  selector: 'jhi-dynamic-form-field',
  templateUrl: './dynamic-form-field.component.html',
  styleUrls: ['./dynamic-form-field.component.scss'],
})
export class DynamicFormFieldComponent implements OnInit {
  @ViewChild(DYNAMIC_FIELD)
  field?: DynamicFormFunctions;

  @Input() formItem!: IDynamicFormFieldModel;

  form!: FormGroup;

  constructor(private rootFormGroup: FormGroupDirective) {}

  ngOnInit(): void {
    this.form = this.rootFormGroup.control;
  }

  remove(): void {
    this.field?.remove();
  }
}
