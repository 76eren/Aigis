import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {catchError, Observable} from "rxjs";
import {UserModel} from "../../../models/user.model";
import {ApiResponse} from "../auth.service";

@Injectable({
  providedIn: 'root',
})
export class UserService {

  constructor(private apiService: ApiService, private toastr: ToastrService) {
  }

  public getCurrentSignedInUser(): Observable<ApiResponse<UserModel>> {
    return this.apiService.get<ApiResponse<UserModel>>('/user/me').pipe(
      catchError((error) => {
        console.error('Womp womp', error);
        throw error;
      })
    );
  }

}
