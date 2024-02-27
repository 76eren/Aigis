import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {PostModel} from "../../models/post.model";
import {UserModel} from "../../models/user.model";
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-post',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss'
})
export class PostComponent {
  @Input() post?: PostModel;
  @Input() user?: UserModel;

  constructor(private datePipe: DatePipe) {}


  public getDate() {
    const date = new Date(this.post?.date!);
    return this.datePipe.transform(date, 'MMM dd - HH:mm') || '';
  }
}
