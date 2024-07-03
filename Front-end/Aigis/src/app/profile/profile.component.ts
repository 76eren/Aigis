import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ApiService} from "../shared/service/api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {lastValueFrom, Subscription} from "rxjs";
import {AuthService} from "../shared/service/auth.service";
import {UserModel} from "../models/user.model";
import {PostModel} from "../models/post.model";
import {PostComponent} from "./post/post.component";
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";
import {UserEditModel} from "../models/user-edit.model";
import {ToastrService} from "ngx-toastr";
import {UserService} from "../shared/service/requests/user.service";
import {HttpResponse} from "@angular/common/http";
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
              private authService: AuthService,
              private router: Router,
              private toastr: ToastrService,
              private userService: UserService
  ) {}

  ngOnInit() {
    this.routeSub = this.route.params.subscribe(params => {
      this.id = params['usernameUnique'];
    });



    this.userService.getCurrentSignedInUser().subscribe((user) => {
      this.userSelf = user.payload;
    });

    if (this.id == undefined) {
      // This means that the user is trying to view their own profile
      this.isViewingOwnProfile = true;

      // TODO: This doesn't need to be a sepearate request
      this.userService.getCurrentSignedInUser().subscribe((user) => {
        this.id = user.payload.usernameUnique;
      });

    }
    else {
      this.isViewingOwnProfile = false;
    }

    this.searchForUser();
  }

  public async searchForUser() {
    if (!this.id) {
      return;
    }

    try {
      const user = await lastValueFrom(this.apiService.GetUserById(this.id));
      this.user = user;
      this.username = user.username;
      this.bio = user.about;
      this.profilePicture = user.profilePictureId
        ? this.apiService.GetImage(user.profilePictureId)
        : "../../assets/default-pfp.jpg";

      this.posts = await lastValueFrom(this.apiService.GetPostsByUserId(user.usernameUnique));

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

      this.apiService.AssignPfp(this.user?.usernameUnique!, formData).subscribe((data) => {
      });
    }

    if (this.bio != "" && this.username != "" && this.bio != undefined && this.username != undefined) {
      let userEdit: UserEditModel = {
        username: this.username,
        about: this.bio
      }

      this.apiService.UpdateUser(this.user?.id!, userEdit).subscribe((response) => {
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

    this.apiService.FollowUser(this.user!).subscribe(
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
