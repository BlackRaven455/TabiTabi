import {Component, OnInit} from '@angular/core';
import {PlaceService} from '../../../core/services/trip/place.service';
import {CardComponent} from '../../../shared/components/card/card.component';
import {Subject, Subscription} from 'rxjs';
import {Iplace} from '../../../shared/interfaces/iplace';
import {ProgressBarComponent} from '../../../shared/components/progress-bar/progress-bar.component';
import {PlacesService} from '../../../core/services/trip/places.service';
import {Place} from '../../../shared/interfaces/place';
import {UserService} from '../../../core/services/auth/user.service';
import {User} from '../../../shared/types/user';

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
  places: Place[] = [];
  currentIndex: number = 0;

  constructor(private placeService: PlaceService, private placesService: PlacesService, private userService: UserService,) {
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
        this.currentIndex++;
      }
    }
    this.parentSubject.next(value);
  }

  ngOnInit() {
      this.placesService.getPlaces().subscribe(
        (data: Place[]) => {
          this.places = data;
        },
        (error) => {
          console.error('Ошибка получения данных:', error);
        }
      );
    this.placeList = this.placeService.getPlaces();
  }

  // placeIsLiked(placeId: number, isLiked: boolean) {
  //   console.log(placeId, isLiked);
  //   this.userService.setUserPreference(placeId, isLiked).subscribe({
  //     next: (response) => {
  //       console.log('Preference updated successfully:', response);
  //     },
  //     error: (error) => {
  //       console.error('Failed to update preference:', error);
  //     }
  //   });
  // }
  receiveData($event: number) {

  }
}
