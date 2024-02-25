import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ApiService} from "../shared/service/api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {AuthService} from "../shared/service/auth.service";
import {UserModel} from "../models/user.model";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  private routeSub: Subscription = new Subscription();
  private id?: string;
  private isViewingOwnProfile?: boolean;
  private user?: UserModel;

  constructor(private apiService: ApiService, private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit() {
    this.routeSub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });

    if (this.id == undefined) {
      // This means that the user is trying to view their own profile
      this.isViewingOwnProfile = true;
      this.id = this.authService.getId();
      console.log(this.id)

    }
    else {
      // This means that the user is trying to view another user's profile
      this.isViewingOwnProfile = false;
    }
    this.searchForUser();
  }

  public searchForUser()  {
    if (this.id == undefined || this.id == "") {
      return;
    }

    this.apiService.GetUserById(this.id).subscribe((data) => {
      this.user = data;

    });
  }
}