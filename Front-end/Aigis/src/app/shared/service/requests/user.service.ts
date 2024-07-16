import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {catchError, Observable} from "rxjs";
import {UserModel} from "../../../models/user.model";
import {ApiResponse} from "../auth.service";
import {UserEditModel} from "../../../models/user-edit.model";
import {map} from "rxjs/operators";
import {z} from "zod";

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

  public Register(payload: { usernameUnique: string; password: string, username: string }) {
    return this.apiService.post<any>('/user/register', { body: payload }).pipe(
      map((data) => {
        return z
          .object({
            payload: z.object({
              token: z.string(),
            }),
          })
          .parse(data);
      }),
    );
  }

  public GetUserById(id: string): Observable<UserModel> {
    return this.apiService.get<any>(`/user/${id}`).pipe(
      map((data) => {
        return z
          .object({
            payload: z.object({
              id: z.string(),
              username: z.string(),
              usernameUnique: z.string(),
              about: z.string().nullable(),
              role: z.string(),
              profilePictureId: z.string().nullable(),
              following: z.array(z.object({
                id: z.string(),
                username: z.string(),
                usernameUnique: z.string(),
                about: z.string().nullable(),
                role: z.string(),
                profilePictureId: z.string().nullable(),
              })).nullable(),
              followers: z.array(z.object({
                id: z.string(),
                username: z.string(),
                usernameUnique: z.string(),
                about: z.string().nullable(),
                role: z.string(),
                profilePictureId: z.string().nullable(),
              })).nullable(),
            }),
          })
          .parse(data);
      }),
      map((data) => {
        return new UserModel(
          data.payload.id,
          data.payload.username,
          data.payload.usernameUnique,
          data.payload.role,
          data.payload.about || 'This user does not have a biography yet',
          data.payload.profilePictureId || '',
          data.payload.following || [],
          data.payload.followers || []
        );
      })
    );
  }

  public AssignPfp(usernameUnique: String, formData: FormData) {
    return this.apiService.patch(`/user/${usernameUnique}`, { body: formData});
  }

  public FollowUser(target: UserModel) {
    return this.apiService.patch(`/user/follow/${target.usernameUnique}`, { });
  }

}
