import {Component, OnInit} from '@angular/core';
import {PlaceService} from '../../../core/services/place.service';
import {CardComponent} from '../../../shared/components/card/card.component';
import {Subject} from 'rxjs';
import {Iplace} from '../../../shared/interfaces/iplace';
import {ProgressBarComponent} from '../../../shared/components/progress-bar/progress-bar.component';

@Component({
  selector: 'app-game-page',
  imports: [
    CardComponent,
    ProgressBarComponent
  ],
  templateUrl: './game-page.component.html',
  styleUrl: './game-page.component.css'
})
export class GamePageComponent implements OnInit {
  parentSubject: Subject<string> = new Subject();
  placeList: Iplace[] = [];
  currentIndex: number = 0;

  constructor(private placeService: PlaceService) {
  }

  cardAnimation(value: string) {
    if (value === 'swiperight') {
      if (this.currentIndex === this.placeList.length) {
        this.currentIndex = 0;
      }
      this.currentIndex++;

    } else if (value === 'swipeleft') {
      if (this.currentIndex === 0) {
        this.currentIndex = this.placeList.length;
      } else {
        this.currentIndex--;
      }
    }
    this.parentSubject.next(value);
  }

  ngOnInit() {
    this.placeList = this.placeService.getPlaces();
  }
}
