import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../../core/services/auth/user.service';
import {NgForOf} from '@angular/common';
import {UserPreference} from '../../../shared/types/user-preference'

@Component({
  selector: 'app-profile',
  imports: [
    NgForOf
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  userPreference: UserPreference[] = [];
  constructor(private route: ActivatedRoute, private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
   this.userService.getUserPreference().subscribe(userPreference => {
      this.userPreference = userPreference;
    });
  }

  logout() {
    this.userService.logout();
    this.router.navigate(['login']);
  }
}
