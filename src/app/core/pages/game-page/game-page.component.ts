import {Component, OnInit} from '@angular/core';
import {PlaceService} from '../../services/place.service';
import {CardComponent} from '../../components/card/card.component';
import {Subject} from 'rxjs';
import {Iplace} from '../../../shared/interfaces/iplace';

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
  placeList: Iplace[] = [];

  constructor(private placeService: PlaceService) {
  }

  cardAnimation(value: string) {
    this.parentSubject.next(value);
  }

  ngOnInit() {
    this.placeList = this.placeService.getPlaces();
  }
}
