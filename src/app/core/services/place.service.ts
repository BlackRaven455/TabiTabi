import {Injectable} from '@angular/core';
import {placeList} from '../../shared/types/placeList';
import {IPlace} from '../../shared/interfaces/IPlace';

@Injectable({
  providedIn: 'root'
})
export class PlaceService {
  placeList: IPlace[] = placeList.getPlaceList();

  constructor() {
  }

  getPlaces(): IPlace[] {
    return this.placeList;
  }

  getPlace(placeId: number) {
    return this.placeList.find(place => placeId === placeId);
  }

  addPlace(place: IPlace) {
    this.placeList.push(place);
  }

  removePlace(place: IPlace) {
    this.placeList = this.placeList.filter(place => place.id !== place.id);
  }
  
}
