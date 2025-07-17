import {Injectable} from '@angular/core';
import {placeList} from '../../../shared/types/placeList';
import {Iplace} from '../../../shared/interfaces/iplace';

@Injectable({
  providedIn: 'root'
})
export class PlaceService {
  placeList: Iplace[] = placeList.getPlaceList();

  constructor() {
  }

  getPlaces(): Iplace[] {
    return this.placeList;
  }

  getPlace(placeId: number) {
    return this.placeList.find(place => placeId === placeId);
  }

  addPlace(place: Iplace) {
    this.placeList.push(place);
  }

  removePlace(place: Iplace) {
    this.placeList = this.placeList.filter(place => place.id !== place.id);
  }

}
