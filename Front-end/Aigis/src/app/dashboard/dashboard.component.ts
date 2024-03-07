import {Component, ElementRef, ViewChild} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ApiService} from "../shared/service/api.service";
import {UserModel} from "../models/user.model";
import {AuthService} from "../shared/service/auth.service";
import {LucideAngularModule} from "lucide-angular";
import {FormsModule} from "@angular/forms";
import {Toast, ToastrService} from "ngx-toastr";
import {catchError} from "rxjs";


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})

export class DashboardComponent {
  public user?: UserModel;
  public text?: string;
  public fileName: string = ""
  @ViewChild('fileInput') fileInput!: ElementRef;


  constructor(private apiService: ApiService, private authService: AuthService, private toastr: ToastrService) {}

  ngOnInit() {
    this.apiService.GetUserById(this.authService.getId()).subscribe((user: UserModel) => {
      this.user = user;
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
