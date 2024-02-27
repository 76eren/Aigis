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

  public imageUrl: String = "";
  public hasImage: boolean = true;

  public profilePictureUrl: String = "";

  constructor(private datePipe: DatePipe) {}

  ngOnInit() {
    if (this.post?.imageId && this.post?.imageId !== '') {
      this.imageUrl = `http://localhost:8080/api/v1/image/direct/${this.post?.imageId}`;
    } else {
      this.hasImage = false;
    }

    if (this.user?.profilePictureId && this.user?.profilePictureId !== '') {
      this.profilePictureUrl = `http://localhost:8080/api/v1/image/direct/${this.user?.profilePictureId}`;
    }
    else {
      this.profilePictureUrl = '../../assets/default-profile-picture.png';
    }
  }

  public getDate() {
    const date = new Date(this.post?.date!);
    return this.datePipe.transform(date, 'MMM dd - HH:mm') || '';
  }
}
