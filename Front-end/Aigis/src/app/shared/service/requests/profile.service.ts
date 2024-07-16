import {Injectable} from "@angular/core";
import {ApiService} from "../api.service";
import {ToastrService} from "ngx-toastr";
import {catchError, Observable} from "rxjs";
import {UserModel} from "../../../models/user.model";
import {ApiResponse} from "../auth.service";

@Injectable({
  providedIn: 'root',
})
export class ProfiuService {

  constructor(private apiService: ApiService, private toastr: ToastrService) {
  }



}
