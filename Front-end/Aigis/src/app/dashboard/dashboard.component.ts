import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ApiService} from "../shared/service/api.service";
import {UserModel} from "../models/user.model";
import {AuthService} from "../shared/service/auth.service";
import {LucideAngularModule} from "lucide-angular";


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})

export class DashboardComponent {
  public user?: UserModel;


  constructor(private apiService: ApiService, private authService: AuthService) {}

  ngOnInit() {
    this.apiService.GetUserById(this.authService.getId()).subscribe((user: UserModel) => {
      this.user = user;
    });
  }

}
