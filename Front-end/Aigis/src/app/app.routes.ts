import { Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {AppLayoutComponent} from "./app-layout/app-layout.component";
import {LoginComponent} from "./login/login.component";
import {loginGuard} from "./shared/guard/login.guard";
import {RegisterComponent} from "./register/register.component";
import {ProfileComponent} from "./profile/profile.component";
import {NotLoginGuard} from "./shared/guard/not-login.guard";
import {EditProfileComponent} from "./profile/edit-profile/edit-profile.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [loginGuard],
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [loginGuard],
  },
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [NotLoginGuard]
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'profile/:usernameUnique',
        component: ProfileComponent,
        canActivate: [NotLoginGuard]
      },
      {
        path: 'profile',
        component: ProfileComponent,
        canActivate: [NotLoginGuard]
      },
      {
        path: 'edit/:usernameUnique',
        component: EditProfileComponent,
        canActivate: [NotLoginGuard]
      }

    ],
  },
];
