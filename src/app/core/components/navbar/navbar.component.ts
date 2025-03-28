import {Component} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MenuComponent} from '../menu/menu.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [
    MenuComponent,
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  animations: [
    trigger('openClose', [
      state('closed', style({
        transform: 'translateX(120%)',
        opacity: 0, display: 'none'
      })),
      state('opened', style({
        transform: 'translateX(0)',
        opacity: 1,
      })),
      transition('closed <=> open', [animate('1s ease-in')])
    ])
  ]
})
export class NavbarComponent {
  menuState: 'opened' | 'closed' = 'closed';

  toggleOpen = () => {
    this.menuState = this.menuState === 'closed' ? 'opened' : 'closed';
  }
}
