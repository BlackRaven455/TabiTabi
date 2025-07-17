import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {Place} from '../../../shared/interfaces/place';
import {PlacesService} from '../../../core/services/trip/places.service';
import {UserService} from '../../../core/services/auth/user.service';
import {debounceTime, distinctUntilChanged, filter, Subject, takeUntil} from 'rxjs';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-places-page',
  imports: [
    NgForOf,
    FormsModule,
    NgIf
  ],
  templateUrl: './places-page.component.html',
  styleUrl: './places-page.component.css'
})
export class PlacesPageComponent implements OnInit, OnDestroy {
  places: Place[] = [];
  filteredPlaces: Place[] = [];
  searchTerm: string = '';
  isLoading: boolean = false;
  isUpdatingPreference: boolean = false;

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(
    private placesService: PlacesService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadPlaces();
    this.setupSearch();
  }

  private loadPlaces() {
    this.isLoading = true;
    this.placesService.getPlaces().subscribe({
      next: (data: Place[]) => {
        this.places = data;
        this.filteredPlaces = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Ошибка получения данных:', error);
        this.isLoading = false;
      }
    });
  }

  private setupSearch() {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(searchTerm => {
        this.filterPlaces(searchTerm);
      });
  }

  onSearchInput() {
    this.searchSubject.next(this.searchTerm);
  }

  performSearch() {
    this.filterPlaces(this.searchTerm);
  }

  private filterPlaces(searchTerm: string) {
    const term = searchTerm.trim().toLowerCase();

    if (term) {
      this.filteredPlaces = this.places.filter(place =>
        place.name.toLowerCase().includes(term) ||
        place.description.toLowerCase().includes(term)
      );
    } else {
      this.filteredPlaces = this.places;
    }
  }

  placeIsLiked(placeId: number, isLiked: boolean) {
    this.isUpdatingPreference = true;

    this.userService.setUserPreference(placeId, isLiked).subscribe({
      next: (response) => {
        console.log('Preference updated successfully:', response);
        this.isUpdatingPreference = false;
      },
      error: (error) => {
        console.error('Failed to update preference:', error);
        this.isUpdatingPreference = false;
      }
    });
  }

  trackByPlaceId(index: number, place: Place): number {
    return place.placeId;
  }


  clearSearch() {
    this.searchTerm = '';
    this.filteredPlaces = this.places;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
