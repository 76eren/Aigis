import {Injectable} from "@angular/core";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import { map, catchError } from 'rxjs/operators';
import { of, Observable } from 'rxjs';
import {ApiService} from "./api.service";
import {UserModel} from "../models/user.model";

export interface ApiResponse<T> {
  payload: T;
  message: string;
  statusCode: string;
}

export interface ApiResponse<T> {
  payload: T;
  message: string;
  statusCode: string;
}

@Injectable()
export class AuthService {
  constructor(private toastr: ToastrService, private router: Router, private http: HttpClient, private apiService: ApiService) {}


  public isIdOfLoggedInUser(id: string): Observable<boolean> {
    let endpoint = '/auth/checkId/' + id;

    return this.apiService.get<any>(endpoint)
      .pipe(map(response => {
        if (response.payload && response.payload.idOfSelf) {
          return true;
        }
        else {
          return false;
        }
      }));
  }


  public isAuthenticated(): Observable<boolean> {
    let endpoint = '/auth/authenticated';

    return this.apiService.get<any>(endpoint)
      .pipe(
        map(response => {
          if (response.payload && response.payload.authenticated) { // TODO: validate response payload
            return true;
          }
          else {
            return false;
          }
        }),
        catchError(error => {
          return of(false);
        })
      );
  }

  public isAdmin(): Observable<boolean> {
    let endpoint = '/auth/isAdmin';

    return this.apiService.get<any>(endpoint)
      .pipe(
        map(response => {
          if (response.payload && response.payload.admin) {
            return true;
          }
          else {
            return false;
          }
        }),
        catchError(error => {
          return of(false);
        })
      );
  }

  public signout(): void {
    this.apiService.post<any>('/auth/logout')
      .subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.toastr.error('Er is iets misgegaan tijdens het uitloggen. \nProbeer het opnieuw.');
        }
      });
  }

}
