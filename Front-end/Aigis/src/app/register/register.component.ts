import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ApiService} from "../shared/service/api.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  username: string = "";
  password: string = "";
  usernameUnique: string = "";

  constructor(private apiService: ApiService, private toastr: ToastrService, private router: Router) {}

  submitRegister() {
    // Before we do anything we check if the username and the usernameunique input fields contain special characters
    if (this.username.match(/[^a-zA-Z0-9]/)) {
      this.toastr.error('Username contains special characters', 'Error');
      return;
    }

    if (this.username != "" && this.password != "" && this.usernameUnique != "") {
      this.toastr.error('Womp womp', 'Error');
      return;
    }

    this.apiService
        .PostRegister({ username: this.username, password: this.password, usernameUnique: this.usernameUnique })
        .subscribe({
            next: (data) => {
                this.toastr.success('Register successful', 'Success');
                this.router.navigate(['/login']);
            },
            error: (error) => {
                console.error(error);
                this.toastr.error('Unknown error occured', 'Error');
            },
        });
  }
}
