import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavbarComponent} from './layout/navbar/navbar.component';
import {FooterComponent} from './layout/footer/footer.component';
import {GamePageComponent} from './features/pages/game-page/game-page.component';
import {NgOptimizedImage, NgStyle} from '@angular/common';
import {UserService} from './core/services/auth/user.service';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, FooterComponent, NgStyle],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'TabiTabi';

  constructor(private http: HttpClient, private userService: UserService) { }
  ngOnInit() {
    console.log('User logged-in:', this.userService.checkIfLoggedIn());
  }
}
