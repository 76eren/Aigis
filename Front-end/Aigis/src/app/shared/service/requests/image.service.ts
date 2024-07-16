import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  constructor(private apiService: ApiService, private toastr: ToastrService, private router: Router) {
  }

  public GetImage(id: String) {
    return (`${ApiService.API_URL}/image/direct/${id}`);
  }

}
