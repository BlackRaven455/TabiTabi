import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavbarComponent} from './core/components/navbar/navbar.component';
import {FooterComponent} from './core/components/footer/footer.component';
import {GamePageComponent} from './core/pages/game-page/game-page.component';
import {NgOptimizedImage, NgStyle} from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, FooterComponent, NgStyle],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'TabiTabi';
}
