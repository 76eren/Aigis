import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable, OperatorFunction } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { z } from 'zod';
import { AuthService } from './auth.service';
import {UserModel} from "../../models/user.model";
import {PostModel} from "../../models/post.model";
import {UserEditModel} from "../../models/user-edit.model";


@Injectable({
  providedIn: 'root',
})
export class ApiService {
  public static API_URL = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {
  }

  public get<T>(path: string, options?: { headers?: HttpHeaders, params?: HttpParams }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();
    let requestParams: HttpParams = options?.params ?? new HttpParams();

    return this.http.get<T>(`${ApiService.API_URL}${path}`, { headers: requestHeaders, params: requestParams, withCredentials: true });
  }

  public post<T>(path: string, options?: { headers?: HttpHeaders, body?: object }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.post<T>(`${ApiService.API_URL}${path}`, options?.body, { headers: requestHeaders, withCredentials: true })
  }

  public put<T>(path: string, options?: {headers?: HttpHeaders, body?: object}): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.put<T>(`${ApiService.API_URL}${path}`, options?.body, { headers: requestHeaders, withCredentials: true })
  }

  public patch<T>(path: string, options?: { headers?: HttpHeaders, body?: object }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.patch<T>(`${ApiService.API_URL}${path}`, options?.body, { headers: requestHeaders, withCredentials: true });
  }

  public delete<T>(path: string, options?: { headers?: HttpHeaders }): Observable<T> {
    let requestHeaders: HttpHeaders = options?.headers ?? new HttpHeaders();

    return this.http.delete<T>(`${ApiService.API_URL}${path}`, { headers: requestHeaders, withCredentials: true })
  }



  public PostRegister(payload: { usernameUnique: string; password: string, username: string }) {
    return this.post<any>('/user/register', { body: payload }).pipe(
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
    return this.get<any>(`/user/${id}`).pipe(
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
    return this.get<any>(`/post/${id}`, {}).pipe(
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
    return this.patch(`/user/${usernameUnique}`, { body: formData});
  }

  public UpdateUser(id: string, userData: UserEditModel) {
    return this.put(`/user/${id}`, { body: userData});
  }

  public CreatePost(userId: string, content: string, image: File | null) {
    const formData = new FormData();
    const postContent = JSON.stringify({ content: content });
    formData.append('post', postContent);

    if (image) {
      formData.append('image', image);
    }

    return this.post(`/post/create/${userId}`, { body: formData});
  }

  public GetImage(id: String) {
    return (`${ApiService.API_URL}/image/direct/${id}`);  }

  public FollowUser(target: UserModel) {
    return this.patch(`/user/follow/${target.usernameUnique}`, { });
  }
}
