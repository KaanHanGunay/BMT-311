import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { BackButtonComponent } from './back-button/back-button.component';
import { CardWrapperComponent } from './card-wrapper/card-wrapper.component';
import { RemoveItemDialogComponent } from './dialogs/remove-item-dialog/remove-item-dialog.component';
import { DynamicFormCreatorComponent } from './dynamic-form-creator/dynamic-form-creator.component';
import { DynamicFormFieldComponent } from './dynamic-form-creator/dynamic-form-field/dynamic-form-field.component';
import { LoadingSpinnerComponent } from './loading-spinner/loading-spinner.component';

@NgModule({
  declarations: [
    BackButtonComponent,
    CardWrapperComponent,
    RemoveItemDialogComponent,
    DynamicFormCreatorComponent,
    DynamicFormFieldComponent,
    LoadingSpinnerComponent,
  ],
  imports: [CommonModule, SharedModule],
})
export class ComponentsModule {}
