import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {catchError, Observable} from "rxjs";
import {UserModel} from "../../../models/user.model";
import {ApiResponse} from "../auth.service";
import {PostModel} from "../../../models/post.model";
import {map} from "rxjs/operators";
import {z} from "zod";

@Injectable({
  providedIn: 'root',
})
export class PostService {

  constructor(private apiService: ApiService, private toastr: ToastrService) {
  }

  public CreatePost(userId: string, content: string, image: File | null) {
    const formData = new FormData();
    const postContent = JSON.stringify({ content: content });
    formData.append('post', postContent);

    if (image) {
      formData.append('image', image);
    }

    return this.apiService.post(`/post/create/${userId}`, { body: formData});
  }

  public GetPostsByUserId(id: string): Observable<PostModel[]> {
    return this.apiService.get<any>(`/post/${id}`, {}).pipe(
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



}
