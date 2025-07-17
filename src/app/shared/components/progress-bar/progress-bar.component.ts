import {Component, Input} from '@angular/core';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-progress-bar',
  imports: [
    DecimalPipe
  ],
  templateUrl: './progress-bar.component.html',
  styleUrl: './progress-bar.component.css'
})
export class ProgressBarComponent {
  @Input() current = 1;
  @Input() total = 1;

  get percentage(): number {
    return (this.current / this.total) * 100;

  }
}
