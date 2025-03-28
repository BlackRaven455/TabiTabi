import {Routes} from '@angular/router';
import {HomePageComponent} from './core/pages/home-page/home-page.component';
import {GamePageComponent} from './core/pages/game-page/game-page.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    component: HomePageComponent,
  },
  {
    path: 'game',
    component: GamePageComponent,
  }
];
