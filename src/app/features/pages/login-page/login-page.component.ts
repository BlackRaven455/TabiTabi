import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Router, RouterLink} from '@angular/router';
import {UserService} from '../../../core/services/auth/user.service';

@Component({
  selector: 'app-login-page',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent implements OnInit {
  loginForm: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
  });

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit() {
    if(this.userService.checkIfLoggedIn()){
      this.router.navigate(['/profile']);
    }
  }
  login(){
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.userService.login( email, password ).subscribe({
        next: (response) => {
          console.log('Login response:', response);
          this.userService.persistUser(response);
          console.log('Login successfully');
          this.router.navigate(['/profile']);
        },
        error: () => {
          console.log('Login failed');
        }
      })
    }

  }

}
