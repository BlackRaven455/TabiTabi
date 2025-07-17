import { Component } from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserService} from '../../../core/services/auth/user.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-registration-page',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './registration-page.component.html',
  styleUrl: './registration-page.component.css'
})
export class RegistrationPageComponent {
  registraionForm: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required,Validators.email]),
    password: new FormControl('', [Validators.required,Validators.minLength(6)]),
    firstName: new FormControl('', [Validators.required,Validators.minLength(1)]),
    lastName: new FormControl('', [Validators.required,Validators.minLength(1)]),
    }
  );

  constructor(private userService: UserService,private router: Router) {
  }

  register() {
    if(this.registraionForm.invalid){
      console.log(JSON.stringify(this.registraionForm.value));
    }
    this.userService.registerUser(this.registraionForm.value).subscribe();
    this.router.navigate(['/login']);
  }


}
