import { Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {AppLayoutComponent} from "./app-layout/app-layout.component";
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";
import {ProfileComponent} from "./profile/profile.component";
import {SearchForUserComponent} from "./search-for-user/search-for-user.component";
import {AuthGuard} from "./shared/guard/auth.guard";
import {LoginGuard} from "./shared/guard/login.guard";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [LoginGuard],
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [LoginGuard],
  },
  {
    path: '',
    component: AppLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'profile/:usernameUnique',
        component: ProfileComponent,
      },
      {
        path: 'profile',
        component: ProfileComponent,
      },
      {
        path: 'search',
        component: SearchForUserComponent,
      }
    ],
  },
  {
    path: '**',
    redirectTo: 'dashboar' +
      '',
  },
];
