import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterOutlet } from '@angular/router';
import {AuthService} from "../shared/service/auth.service";

@Component({
  selector: 'app-app-layout',
  standalone: true,
  templateUrl: './app-layout.component.html',
  styleUrl: './app-layout.component.scss',
  imports: [CommonModule, NavbarComponent, RouterOutlet],
})
export class AppLayoutComponent {
  public isAdmin: boolean = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.validateToken();
    this.isAdmin = this.authService.isAdmin();
  }

}
