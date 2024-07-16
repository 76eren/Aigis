import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ApiService} from "../shared/service/api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {lastValueFrom, Subscription} from "rxjs";
import {AuthService} from "../shared/service/auth.service";
import {UserModel} from "../shared/models/user.model";
import {PostModel} from "../shared/models/post.model";
import {PostComponent} from "./post/post.component";
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";
import {UserEditModel} from "../shared/models/user-edit.model";
import {ToastrService} from "ngx-toastr";
import {UserService} from "../shared/service/requests/user.service";
import {HttpResponse} from "@angular/common/http";
import {PostService} from "../shared/service/requests/post.service";
import {ImageService} from "../shared/service/requests/image.service";
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, PostComponent, LucideAngularModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})

export class ProfileComponent {
  private routeSub: Subscription = new Subscription();
  private id?: string;
  public isViewingOwnProfile?: boolean;
  public user?: UserModel;
  public profilePicture: string = "";
  public userFound: boolean = true;
  public posts: PostModel[] = [];

  public userSelf?: UserModel = undefined;
  public isFollowing: boolean = false;

  // Edit mdoe
  public isEditMode: boolean = false;
  @Input() public username?: string;
  @Input() public bio?: string;
  @Input() image?: string;
  @ViewChild('docpicker') docpicker!: ElementRef;


  constructor(private apiService: ApiService,
              private route: ActivatedRoute,
              private toastr: ToastrService,
              private userService: UserService,
              private postService: PostService,
              private imageService: ImageService
  ) {}

  ngOnInit() {
    this.routeSub = this.route.params.subscribe(params => {
      this.id = params['usernameUnique'];

      this.userService.getCurrentSignedInUser().subscribe((user) => {
        this.userSelf = user.payload;

        if (this.id == undefined) {
          this.isViewingOwnProfile = true;
          this.id = this.userSelf?.usernameUnique;
        }
        else {
          this.isViewingOwnProfile = false;
        }

        this.searchForUser();

      });
    });

  }

  public async searchForUser() {
    if (!this.id) {
      return;
    }

    try {
      const user = await lastValueFrom(this.userService.GetUserById(this.id));
      this.user = user.payload;
      this.username = this.user.username;
      this.bio = this.user.about;
      this.profilePicture = this.user.profilePictureId
        ? this.imageService.GetImage(this.user.profilePictureId)
        : "../../assets/default-pfp.jpg";

      let postsApiresonse = await lastValueFrom(this.postService.GetPostsByUserId(this.user.usernameUnique));
      this.posts = postsApiresonse.payload;

      // If the user is not viewing their own profile, we need to check if they are following the user
      if (!this.isViewingOwnProfile) {
        this.userSelf!.following?.forEach((user) => {
          if (this.user?.usernameUnique! == user.usernameUnique) {
            this.isFollowing = true;
          }
        });
      }

    }
    catch (error: unknown) {
      if (typeof error === "object" && error !== null && "status" in error) {
        const status = (error as { status: number }).status;
        if (status === 404) {
          this.userFound = false;
          this.profilePicture = "../../assets/default-pfp.jpg";
        }
        else {
          console.error('Error fetching data', error);
        }
      }
    }
  }

  editProfile() {
    this.isEditMode = !this.isEditMode;
  }

  onSubmitClick() {
    if (this.docpicker.nativeElement.files.length > 0) {
      // TODO: Move this logic to the api service
      let formData: FormData = new FormData();
      let file = this.docpicker.nativeElement.files[0];
      if (file && file instanceof Blob) {
        formData.append('image', file, 'image.jpg'); // Uh oh, hard coding an extension
      }

      this.userService.AssignPfp(this.user?.usernameUnique!, formData).subscribe((data) => {
      });
    }

    if (this.bio != "" && this.username != "" && this.bio != undefined && this.username != undefined) {
      let userEdit: UserEditModel = {
        username: this.username,
        about: this.bio
      }

      this.userService.UpdateUser(this.user?.id!, userEdit).subscribe((response) => {
        location.reload();
      });
    }

  }

  follow() {
    if (this.user == undefined) {
      return;
    }

    let initial: boolean = this.isFollowing;
    this.isFollowing = !this.isFollowing;

    this.userService.FollowUser(this.user!).subscribe(
      (data) => {
        this.toastr.success('User followed/unfollowed', 'Success');
      },
      (error) => {
        this.toastr.error('Error following/unfollowing user', 'Error');
        this.isFollowing = initial;
      }
    );
  }
}
