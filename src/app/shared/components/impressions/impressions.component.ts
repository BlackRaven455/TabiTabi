import {Component} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-impressions',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './impressions.component.html',
  styleUrl: './impressions.component.css'
})
export class ImpressionsComponent {
  image: string = 'assets/images/icons8-user-100.png';
}
