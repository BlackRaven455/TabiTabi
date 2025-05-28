import {Component, EventEmitter, Output} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {NgForOf} from '@angular/common';
import {IPage} from '../../../shared/interfaces/ipage';

@Component({
  selector: 'app-menu',
  imports: [
    RouterLink,
    NgForOf
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css',
})
export class MenuComponent {
  @Output() close = new EventEmitter<void>();
  pages: IPage[] = [];

  constructor(private router: Router) {
    this.pages = this.router.config
      .filter(route => route.path) // Exclude wildcard or empty path routes
      .map(route => ({
        title: route.path!.charAt(0).toUpperCase() + route.path!.slice(1), // Capitalize
        link: `/${route.path}`,
        active: false, // This can be controlled dynamically
      }));
  }
}
