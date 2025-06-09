import {Component} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {SignUpFormComponent} from '../../shared/components/sign-up-form/sign-up-form.component';
import {routes} from '../../app.routes';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [
    SignUpFormComponent,
    NgForOf,
    RouterLink,
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  animations: []
})
export class NavbarComponent {
  protected readonly routes = routes;

}
