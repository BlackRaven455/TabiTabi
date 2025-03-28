import {Component, OnInit} from '@angular/core';
import {NavbarComponent} from '../../components/navbar/navbar.component';
import {IPlace} from '../../../shared/interfaces/IPlace';
import {PlaceService} from '../../services/place.service';
import {NgForOf, NgOptimizedImage} from '@angular/common';
import {Subject} from 'rxjs';
import {CardComponent} from '../../components/card/card.component';

@Component({
  selector: 'app-game-page',
  imports: [
    CardComponent
  ],
  templateUrl: './game-page.component.html',
  styleUrl: './game-page.component.css'
})
export class GamePageComponent implements OnInit {
  parentSubject: Subject<string> = new Subject();


  places: IPlace[] = [];

  constructor(private placeService: PlaceService) {
  }

  cardAnimation(value: string) {
    this.parentSubject.next(value);
  }

  ngOnInit() {
    this.places = this.placeService.getPlaces();
  }
}
