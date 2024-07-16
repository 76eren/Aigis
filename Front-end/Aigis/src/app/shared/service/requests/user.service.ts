import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {catchError, Observable} from "rxjs";
import {UserModel} from "../../models/user.model";
import {ApiResponse} from "../auth.service";
import {UserEditModel} from "../../models/user-edit.model";
import {map} from "rxjs/operators";
import {z} from "zod";
import {UserRegisterModel} from "../../models/user-register.model";

@Injectable({
  providedIn: 'root',
})
export class UserService {

  constructor(private apiService: ApiService, private toastr: ToastrService) {
  }

  public getCurrentSignedInUser(): Observable<ApiResponse<UserModel>> {
    return this.apiService.get<ApiResponse<UserModel>>('/user/me').pipe(
      catchError((error) => {
        throw error;
      })
    );
  }

  public UpdateUser(id: string, userData: UserEditModel) {
    return this.apiService.put(`/user/${id}`, { body: userData});
  }

  public Register(payload: { usernameUnique: string; password: string, username: string }): Observable<ApiResponse<UserRegisterModel>> {
    return this.apiService.post<ApiResponse<UserRegisterModel>>('/user/register', { body: payload })
  }

  public GetUserById(id: string): Observable<ApiResponse<UserModel>> {
    return this.apiService.get<ApiResponse<UserModel>>(`/user/${id}`)
  }

  public AssignPfp(usernameUnique: String, formData: FormData) {
    return this.apiService.patch(`/user/${usernameUnique}`, { body: formData});
  }

  public FollowUser(target: UserModel) {
    return this.apiService.patch(`/user/follow/${target.usernameUnique}`, { });
  }

}
