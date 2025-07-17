import {Component} from '@angular/core';
import {NgForOf, NgOptimizedImage} from '@angular/common';
import {OpentripService} from '../../../core/services/trip/opentrip.service';
import {PlacesComponent} from '../../../shared/components/places/places.component';
import {RegistrationPageComponent} from '../registration-page/registration-page.component';
import {PlacesService} from '../../../core/services/trip/places.service';
import {Place} from '../../../shared/interfaces/place';
import {PlacesPageComponent} from '../places-page/places-page.component';

@Component({
  selector: 'app-test-page',
  imports: [
    NgForOf,
    PlacesComponent,
    RegistrationPageComponent,
    NgOptimizedImage,
    PlacesPageComponent
  ],
  templateUrl: './test-page.component.html',
  styleUrl: './test-page.component.css'
})
export class TestPageComponent {

}
