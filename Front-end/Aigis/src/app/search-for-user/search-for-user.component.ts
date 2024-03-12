import {Component, Input} from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-search-for-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-for-user.component.html',
  styleUrl: './search-for-user.component.scss'
})
export class SearchForUserComponent {
  @Input() public input: string = "";

  constructor(private router: Router, private route: ActivatedRoute) {
  }

  onsubmitClick() {
    if (this.input === "") {
      return;
    }

    this.router.navigate(['/profile', this.input]);
  }
}
