import {Routes} from '@angular/router';
import {HomePageComponent} from './features/pages/home-page/home-page.component';
import {GamePageComponent} from './features/pages/game-page/game-page.component';
import {SignUpFormComponent} from './shared/components/sign-up-form/sign-up-form.component';
import {DashboardComponent} from './features/pages/dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    title: 'Home',
    component: DashboardComponent,
  },
  {
    path: 'game',
    title: 'Game',
    component: GamePageComponent,
  },
  {
    path: 'test', title: 'Test',
    component: SignUpFormComponent
  },
];
