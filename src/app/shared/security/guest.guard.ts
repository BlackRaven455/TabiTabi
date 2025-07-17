import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {UserService} from '../../core/services/auth/user.service';

export const guestGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userService = inject(UserService);
  if(userService.checkIfLoggedIn()){
    router.navigate(['/profile']);
    return false;
  }
  return true;
};
