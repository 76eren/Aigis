import { inject } from '@angular/core';
import { AuthService } from "../service/auth.service";
import {map, take} from 'rxjs/operators';
import {Observable} from "rxjs";
import {Router} from "@angular/router";

export const LoginGuard: () => Observable<boolean> = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated().pipe(
    take(1),
    map(isAuthenticated => {
      if (!isAuthenticated) {
        return true;
      }
      else {
        router.navigate(['/dashboard']);
        return false;
      }
    })
  );
};
