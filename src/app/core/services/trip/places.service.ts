import { Injectable } from '@angular/core';
import {Iplace} from '../../../shared/interfaces/iplace';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Place} from '../../../shared/interfaces/place';
import {Observable} from 'rxjs';
import {UserService} from '../auth/user.service';

const PLACES_API = 'http://localhost:8080/api/places';

@Injectable({
  providedIn: 'root'
})
export class PlacesService {

  placeList: Place[] = [];
  constructor(private httpClient: HttpClient, private userService: UserService) { }


  getPlaces(): Observable<Place[]> {
    return this.httpClient.get<Place[]>(`${PLACES_API}/all`, this.userService.getAuthHeader());
  }

  getPlaceById(id: number): Observable<Place> {
    return this.httpClient.get<Place>(`${PLACES_API}/${id}`, this.userService.getAuthHeader());
  }

  addPlace(place: Place): Observable<Place> {
    return this.httpClient.post<Place>(`${PLACES_API}`, place, this.userService.getAuthHeader());
  }

  updatePlace(id: number, place: Place): Observable<Place> {
    return this.httpClient.put<Place>(`${PLACES_API}/${id}`, place, this.userService.getAuthHeader());
  }

  deletePlace(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${PLACES_API}/${id}`, this.userService.getAuthHeader());
  }
}
