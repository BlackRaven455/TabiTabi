import {Routes} from '@angular/router';
import {HomePageComponent} from './features/pages/home-page/home-page.component';
import {GamePageComponent} from './features/pages/game-page/game-page.component';
import {DashboardComponent} from './features/pages/dashboard/dashboard.component';
import {TestPageComponent} from './features/pages/test-page/test-page.component';
import {LoginPageComponent} from './features/pages/login-page/login-page.component';
import {RegistrationPageComponent} from './features/pages/registration-page/registration-page.component';
import {ProfileComponent} from './features/pages/profile/profile.component';
import {loggedInGuard} from './shared/security/logged-in.guard';
import {PlacesPageComponent} from './features/pages/places-page/places-page.component';
import {guestGuard} from './shared/security/guest.guard';



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
    title: 'Lets Travel',
    component: GamePageComponent,
    canActivate: [loggedInGuard],
  },
  {
    path: 'places', title: 'Find Places',
    component: PlacesPageComponent,
    canActivate: [loggedInGuard],
  },
  {
    path: 'profile',
    title: 'Profile',
    component: ProfileComponent,
    canActivate: [loggedInGuard],
  },
  // {
  //   path: 'test', title: 'Test',
  //   component: TestPageComponent,
  //   canActivate: [loggedInGuard],
  // },
  {
    path: 'login', title: 'Login',
    component: LoginPageComponent,
    canActivate: [guestGuard]

  },
  {
    path: 'signup', title: 'Signup',
    component: RegistrationPageComponent,
    canActivate: [guestGuard]

  },

];
