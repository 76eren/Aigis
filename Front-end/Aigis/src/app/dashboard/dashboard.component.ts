import {Component, ElementRef, ViewChild} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ApiService} from "../shared/service/api.service";
import {UserModel, UserSimplifiedModel} from "../models/user.model";
import {AuthService} from "../shared/service/auth.service";
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";
import {Toast, ToastrService} from "ngx-toastr";
import {catchError, lastValueFrom} from "rxjs";
import {PostModel} from "../models/post.model";
import {PostComponent} from "../profile/post/post.component";


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule, PostComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})

export class DashboardComponent {
  public user?: UserModel;
  public text?: string;
  public fileName: string = ""
  @ViewChild('fileInput') fileInput!: ElementRef;

  public posts: PostModel[] = [];
  public users: UserSimplifiedModel[] = [];

  constructor(private apiService: ApiService, private authService: AuthService, private toastr: ToastrService) {}

  ngOnInit() {
    this.SearchForUser().then(() => {
      this.loadPosts();
    });
  }

  async SearchForUser() {
    this.user = await lastValueFrom(this.apiService.GetUserById(this.authService.getId()));
  }


  loadPosts() {
    this.user?.following?.forEach((user: UserSimplifiedModel) => {
      this.apiService.GetPostsByUserId(user.usernameUnique).subscribe((posts: PostModel[]) => {
        if (posts.length != 0) {
          this.posts.push(...posts)
          for (let post of posts) {
            this.users.push(user)
          }
        }
      });
    });

  }


  onSubmit() {
    if (this.text != "" || this.text != null) {

      let file: File | null = null;
      if (this.fileInput.nativeElement.files.length > 0) {
        file = this.fileInput.nativeElement.files[0];
      }

      this.apiService.CreatePost(this.user!.usernameUnique, this.text!, file).pipe(
        catchError((error: any) => {
          if (error.status === 403) {
            this.toastr.error('Post could not be created', 'Error');
          }
          throw error;
        })
      ).subscribe((data) => {
        if (data.status == 201) {
          this.toastr.success('Post created successfully', 'Success');
          this.text = "";
          this.fileInput.nativeElement.value = "";
        }
      });
    }
  }

  fileChanged(event: any) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.fileName = file.name;
      console.log("Selected file:", this.fileName);
    }
  }
}
