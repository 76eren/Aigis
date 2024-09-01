import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {UserService} from "../shared/service/requests/user.service";

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

  constructor(private userService: UserService, private toastr: ToastrService, private router: Router) {}

  submitRegister() {
    this.userService
        .Register({ username: this.username, password: this.password, usernameUnique: this.usernameUnique })
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
