import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ApiService} from "../shared/service/api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {AuthService} from "../shared/service/auth.service";
import {UserModel} from "../models/user.model";
import {PostModel} from "../models/post.model";
import {PostComponent} from "./post/post.component";
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";

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

  // Edit mdoe
  public isEditMode: boolean = true;

  @Input() public username?: string;
  @Input() public bio?: string;
  @Input() image?: string;
  @ViewChild('docpicker') docpicker!: ElementRef;


  constructor(private apiService: ApiService, private route: ActivatedRoute, private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.routeSub = this.route.params.subscribe(params => {
      this.id = params['usernameUnique'];
    });

    if (this.id == undefined) {
      // This means that the user is trying to view their own profile
      this.isViewingOwnProfile = true;
      this.id = this.authService.getId();

    }
    else {
      // This means that the user is trying to view another user's profile
      this.isViewingOwnProfile = false;
    }
    this.searchForUser();
  }

  public searchForUser()  {
    if (this.id == undefined || this.id == "") {
      return;
    }

    this.apiService.GetUserById(this.id).subscribe((
        data) => {
      this.user = data;
      this.username = this.user.username;
      this.bio = this.user.about;


      if (this.user.profilePictureId != null && this.user.profilePictureId != "") {
        this.profilePicture = `http://localhost:8080/api/v1/image/direct/${this.user.profilePictureId}`;
      }
      else {
        this.profilePicture = "../../assets/default-pfp.jpg";
      }

      // Now we retrieve all the user's blogposts
        this.apiService.GetPostsByUserId(this.user.usernameUnique).subscribe((data) => {
            this.posts = data;
            console.log(this.posts);
        });

    },
    (error) => {
      if (error.status == 404) {
        this.userFound = false;
        this.profilePicture = "../../assets/default-pfp.jpg";
      }
    });
  }

  editProfile() {
    this.isEditMode = !this.isEditMode;
  }

  onSubmitClick() {
    if (this.docpicker.nativeElement.files.length > 0) {
      let formData: FormData = new FormData();
      let file = this.docpicker.nativeElement.files[0];
      if (file && file instanceof Blob) {
        formData.append('image', file, 'image.jpg'); // Uh oh, hard coding an extension
      }
      this.apiService.AssignPfp(this.user?.usernameUnique!, formData).subscribe((data) => {

      });
    }


  }

  protected readonly undefined = undefined;
}
