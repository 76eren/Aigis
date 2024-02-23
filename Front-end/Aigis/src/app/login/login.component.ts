import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from "@angular/forms";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ApiService} from "../shared/service/api.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  username: string = "";
  password: string = "";

  constructor(
    private router: Router,
    private toastr: ToastrService,
    private apiService: ApiService) {}

  public submitLogin() {
    this.apiService
      .PostLogin({ usernameUnique: this.username, password: this.password })
      .subscribe({
        next: (data) => {
          this.toastr.success('Login successful', 'Success');
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          console.error(error);
          this.toastr.error('Invalid username or password', 'Error');
        },
      });
  }
}
