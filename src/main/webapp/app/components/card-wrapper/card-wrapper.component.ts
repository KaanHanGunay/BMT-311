import { Component, Input } from '@angular/core';

@Component({
  selector: 'jhi-card-wrapper',
  templateUrl: './card-wrapper.component.html',
  styleUrls: ['./card-wrapper.component.scss'],
})
export class CardWrapperComponent {
  @Input()
  title?: string;

  @Input()
  backButton?: boolean;

  @Input()
  size?: 'full' | 'medium' = 'medium';
}
