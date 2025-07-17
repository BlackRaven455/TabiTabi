import {Component, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {OpentripService} from '../../../core/services/trip/opentrip.service';
import {catchError, forkJoin, of} from 'rxjs';


@Component({
  selector: 'app-places',
  standalone: true,
  imports: [CommonModule, HttpClientModule, NgOptimizedImage],
  templateUrl: './places.component.html',
  styleUrls: ['./places.component.css']
})
export class PlacesComponent implements OnInit {
  places: any[] = [];
  lat = 37.9225;
  lon = 139.0412;
  imageCache = new Map<string, string>();
  validatedPlaces: any[] = [];
  isLoading = false;
  radius = 1000;

  constructor(private otmService: OpentripService) {
  }

  ngOnInit() {
    this.validatedPlaces = this.places;
    // this.getPlaces(null);
  }

  applyFilter(places: { name: string, xid: string, image: string }[]) {
    this.validatedPlaces = places.filter(
      (place) => place.name && place.image
    );
    this.isLoading = false;
  }

  getPlaces(event: any) {
    if (event) {
      event.preventDefault();
    }

    this.isLoading = true;

    this.otmService.getPlacesByRadius(this.lat, this.lon, this.radius).subscribe({
      next: (data: any) => {
        this.places = data.features.slice(0, 200).map((place: any) => ({
          name: place.properties.name,
          xid: place.properties.xid,
          image: '',
          rate: 0,
        }));

        const observables = this.places.map((place) => {
          const cachedImage = this.imageCache.get(place.xid);
          if (cachedImage) {
            place.image = cachedImage;
            return of(null);
          } else {
            return this.otmService.getData(place.xid).pipe(
              catchError((err) => {
                console.warn(`Image not loaded for ${place.xid}`, err);
                return of(null);
              })
            );
          }
        });

        forkJoin(observables).subscribe((responses) => {
          responses.forEach((data, index) => {
            if (data) {
              const place = this.places[index];
              place.image = data?.image || '';
              place.rate = data?.rate || 0;
              this.imageCache.set(place.xid, place.image);
            }
          });

          this.applyFilter(this.places);
          this.isLoading = false;
        });
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }
}
