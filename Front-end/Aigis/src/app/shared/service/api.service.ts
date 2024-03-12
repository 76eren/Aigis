import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable, OperatorFunction } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { z } from 'zod';
import { AuthService } from './auth.service';
import {UserModel} from "../../models/user.model";
import {PostModel} from "../../models/post.model";
import {UserEditModel} from "../../models/user-edit.model";


@Injectable({
  providedIn: 'root',
})export class ApiService {
    public static API_URL = 'http://localhost:8080/api/v1';

    constructor(private http: HttpClient, private authService: AuthService) {
    }

    public PostLogin(payload: { password: string; usernameUnique: string }) {
        return this.http.post(`${ApiService.API_URL}/auth/login`, payload).pipe(
            map((data) => {
                return z
                    .object({
                        payload: z.object({
                            token: z.string(),
                        }),
                    })
                    .parse(data);
            }),
            tap((data) => {
                this.authService.setToken(data.payload.token);
            })
        );
    }

    public PostRegister(payload: { usernameUnique: string; password: string, username: string }) {
        return this.http.post(`${ApiService.API_URL}/user/register`, payload).pipe(
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
    return this.http.get(`${ApiService.API_URL}/user/${id}`).pipe(
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

    public GetPostsByUserId(id: string): Observable<PostModel[]> {
        let token = this.authService.getToken();
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get<any>(`${ApiService.API_URL}/post/${id}`, {headers: headers}).pipe(
            map(response => {
                return response.payload.map((post: any) => {
                    return z
                        .object({
                            id: z.string(),
                            content: z.string(),
                            likes: z.number(),
                            date: z.number(),
                            imageId: z.string().nullable(),
                        })
                        .parse(post);
                });
            }),
            map((data: any[]) => {
                return data.map((post: any) => new PostModel(
                    post.id,
                    post.content,
                    post.likes,
                    post.date,
                    post.imageId || ''
                ));
            })
        );
    }

    public AssignPfp(usernameUnique: String, formData: FormData) {
      let token = this.authService.getToken();
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.patch(`${ApiService.API_URL}/user/${usernameUnique}`, formData, {responseType: 'text', observe: 'response', headers: headers});
    }

    public UpdateUser(id: string, userData: UserEditModel) {
        let token = this.authService.getToken();
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.put(`${ApiService.API_URL}/user/${id}`, userData, {responseType: 'text', observe: 'response', headers: headers});
    }

  public CreatePost(userId: string, content: string, image: File | null) {
    let token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const formData = new FormData();
    const postContent = JSON.stringify({ content: content });
    formData.append('post', postContent);

    if (image) {
      formData.append('image', image);
    }

    return this.http.post(`${ApiService.API_URL}/post/create/${userId}`, formData, {
      responseType: 'text',
      observe: 'response',
      headers: headers
    });
  }

  public GetImage(id: String) {
      return (`${ApiService.API_URL}/image/direct/${id}`);
  }


  public FollowUser(target: UserModel) {
    let token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.patch(`${ApiService.API_URL}/user/follow/${target.usernameUnique}`, {}, {responseType: 'text', observe: 'response', headers: headers});
  }

}
