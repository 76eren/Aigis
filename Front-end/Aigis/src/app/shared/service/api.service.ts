import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, OperatorFunction } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { z } from 'zod';
import { AuthService } from './auth.service';


@Injectable({
  providedIn: 'root',
})export class ApiService {
  public static API_URL = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  PostLogin(payload: { password: string; usernameUnique: string }) {
    return this.http.post(`${ApiService.API_URL}/auth/login`, payload).pipe(
      map((data) => {
        return z
          .object({
            payload: z.object({
              token: z.string(),
            }),
          })
          .parse(data);
      }),
      tap((data) => {
        this.authService.setToken(data.payload.token);
      })
    );
  }

  PostRegister(payload: { usernameUnique: string; password: string, username: string}) {
      return this.http.post(`${ApiService.API_URL}/user/register`, payload).pipe(
          map((data) => {
              return z
                  .object({
                      payload: z.object({
                          token: z.string(),
                      }),
                  })
                  .parse(data);
          }),
      );
  }

}
