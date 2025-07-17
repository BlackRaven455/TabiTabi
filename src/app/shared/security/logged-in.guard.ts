import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {UserService} from '../../core/services/auth/user.service';

export const loggedInGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userService = inject(UserService);
  if (!userService.checkIfLoggedIn()){
    console.log('user not logged in');
    router.navigate(['/']);
    return false;
  }
  console.log('user logged in');
  return true;
};
