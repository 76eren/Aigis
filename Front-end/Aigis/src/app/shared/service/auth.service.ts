import {Injectable} from "@angular/core";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {ApiService} from "./api.service";

@Injectable()
export class AuthService {
  constructor(private toastr: ToastrService, private router: Router, private http: HttpClient) {}


  parseToken = (token: string) => {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64));

    return JSON.parse(jsonPayload);
  };

  public isAuthenticated(): boolean {
    const token = sessionStorage.getItem('token');
    if (!token) return false;

    return true;
  }

  public isAdmin(): boolean {
    const token = sessionStorage.getItem('token');
    if (!token) return false;

    const claims = this.parseToken(token);
    if (claims.role !== 'ADMIN' && claims.role !== 'ADMIN') return false;

    return true;
  }

  public setToken(token: string): void {
    sessionStorage.setItem('token', token);
  }

  public getToken() {
    return sessionStorage.getItem('token');
  }

  public validateToken(): void {
    let isValid = false;
    const token = sessionStorage.getItem('token');
    if (!token) return;

    this.http.get(ApiService.API_URL + `/auth/validate/${token}`, {observe: 'response', responseType : 'text'}).subscribe(
      response => {
        const responseBody = response.body ? response.body.toString() : '';
        if (responseBody.includes('invalid')) {
          isValid = false;
        }
        else if (responseBody.includes("valid")) {
          isValid = true;
        }
        else {
          isValid = false;
        }

        if (!isValid) {
          sessionStorage.removeItem('token');
          this.toastr.error('Your session has expired or is invalid. Please log in again.', 'Session Expired');
          this.router.navigate(['/login']);
        }

      },
    );
  }

  public isLoggedIn(): boolean {
    const token = sessionStorage.getItem('token');
    return !!token;
  }

  public signout(): void {
    sessionStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  public getId() {
    const token = sessionStorage.getItem('token');
    if (!token) return '';

    try {
      // Split the token into parts
      const parts = token.split('.');
      if (parts.length !== 3) {
        throw new Error('Invalid JWT token');
      }
      const payload = parts[1];
      const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      const payloadObj = JSON.parse(decodedPayload);
      return payloadObj.sub;
    }
    catch (error) {
      console.error('Failed to decode JWT:', error);
      return null;
    }

  }

}
