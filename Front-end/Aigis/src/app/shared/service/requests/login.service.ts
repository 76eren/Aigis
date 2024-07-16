import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private apiService: ApiService, private toastr: ToastrService, private router: Router) {
  }

  public login(username: string, password: string): void {
    const loginData = {
      "usernameUnique" : username,
      "password" : password
    };

    this.apiService.post<any>('/auth/login', { body: loginData })
      .subscribe({
        next: (response) => {
          this.router.navigate(['/home']);
        },
        error: (error) => {
          this.toastr.error('Invalid username or password');
        }
      });
  }

}
