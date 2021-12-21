import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { v4 } from 'uuid';

import { IDynamicFormCreatorModel } from './models/dynamic-form-field.model';
import { DynamicFormService } from './dynamic-form/dynamic-form.service';
import { RemoveItemDialogComponent } from '../dialogs/remove-item-dialog/remove-item-dialog.component';
import { DynamicFormFieldComponent } from './dynamic-form-field/dynamic-form-field.component';

@Component({
  selector: 'jhi-dynamic-form-creator',
  templateUrl: './dynamic-form-creator.component.html',
  styleUrls: ['./dynamic-form-creator.component.scss'],
})
export class DynamicFormCreatorComponent implements OnInit, OnDestroy {
  @Input()
  set formCreator(formCreator: IDynamicFormCreatorModel) {
    this._formCreator = this.dynamicFormService.extractFieldArrays(formCreator);
  }

  get formCreator(): IDynamicFormCreatorModel {
    return this._formCreator;
  }

  routeSub?: Subscription;

  private _formCreator!: IDynamicFormCreatorModel;

  constructor(
    private fb: FormBuilder,
    private dynamicFormService: DynamicFormService,
    private _modalService: NgbModal,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.routeSub = this.activatedRoute.queryParams.subscribe(params => {
      this.dynamicFormService.setEntityId(params['id']);
      this.formCreator.fields.forEach(formItem => {
        this.formCreator.form.addControl(formItem.id, this.fb.control(formItem.value, formItem.validators));
      });
    });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  addNewField(index: number): void {
    const newField = { ...this.formCreator.fields[index], value: null };
    const newFieldId = this.dynamicFormService.findId(newField);
    newField.id = newFieldId + '_' + v4();
    this.formCreator.fields.splice(index + 1, 0, newField);
    this.formCreator.form.addControl(newField.id, this.fb.control(newField.value, newField.validators));
  }

  removeField(id: string, dynamicFormFieldComponent: DynamicFormFieldComponent): void {
    this._modalService.open(RemoveItemDialogComponent).result.then(value => {
      if (value) {
        dynamicFormFieldComponent.remove();
        const field = this.formCreator.fields.find(fld => fld.id === id)!;
        const numberOfFields = this.dynamicFormService.findNumberOfMultipleFields(this.formCreator, field);

        if (numberOfFields > 1) {
          this.formCreator.fields = this.formCreator.fields.filter(f => f !== field);
          this.formCreator.form.removeControl(field.id);
        } else {
          const frm = this.formCreator.form.get(field.id)!;

          if (frm instanceof FormControl) {
            frm.setValue(null);
          } else if (frm instanceof FormGroup) {
            frm.reset();
          }
        }
      }
    });
  }
}
