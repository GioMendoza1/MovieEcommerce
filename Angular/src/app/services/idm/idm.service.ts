import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { map, catchError} from 'rxjs/operators';
import { NoContentResponseModel } from 'src/app/models/no-content-response-model';
import { Router } from '@angular/router';
import { RegisterRequestModel } from 'src/app/models/idm/register-request-model';
import { RegisterResponseModel } from 'src/app/models/idm/register-response-model';
import { stringToKeyValue } from '@angular/flex-layout/extended/typings/style/style-transforms';
import { LoginRequestModel } from 'src/app/models/idm/login-request-model';

@Injectable({
  providedIn: 'root'
})
export class IdmService {

  constructor(private http: HttpClient, private router: Router) {}

  public registerRequest(requestModel: RegisterRequestModel) : Promise<NoContentResponseModel> {
    return this.http.post<NoContentResponseModel>(`${environment.apiGatewayURL}/api/g/idm/register`, JSON.stringify(requestModel), { headers: {'Content-Type': 'application/json' } } ).toPromise()
  }

  public loginRequest(requestModel: LoginRequestModel) : Promise<NoContentResponseModel> {
    return this.http.post<NoContentResponseModel>(`${environment.apiGatewayURL}/api/g/idm/login`, JSON.stringify(requestModel), { headers: {'Content-Type': 'application/json' } } ).toPromise()
  }
}
