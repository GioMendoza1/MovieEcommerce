import { Component, ViewChild, AfterViewInit, OnInit, Injectable} from '@angular/core';
import { MatInput } from '@angular/material/input';
import {
  HttpClient
} from '@angular/common/http';
import { interval, Observable } from 'rxjs';
import { map, catchError, first} from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { IdmService } from 'src/app/services/idm/idm.service';
import { RegisterRequestModel } from 'src/app/models/idm/register-request-model';
import { RegisterResponseModel } from 'src/app/models/idm/register-response-model';
import { NoContentResponseModel } from 'src/app/models/no-content-response-model';
import { LoginRequestModel } from 'src/app/models/idm/login-request-model';
import { GatewayService } from 'src/app/services/gateway/gateway.service';
import { LoginResponseModel } from 'src/app/models/idm/login-response-model';
import { FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit, AfterViewInit {

  constructor( 
    private idmService: IdmService,
    private gatewayService: GatewayService
  ){}

  public userName: string = "";
  public password: string = "";

  public userNameControl = new FormControl('', [Validators.required, Validators.email]);
  public passwordControl = new FormControl('', [Validators.required]);


  ngOnInit() {
  }

  ngAfterViewInit() {
    console.log('Values on ngAfterViewInit():');    
  }

  public getErrorMessage(control: FormControl) {
    if (control.hasError('required')) {
      return 'You must enter a value';
    }
    return control.hasError('email') ? 'Not a valid email' : '';
  }

  public async onClick(): Promise<void> {
    let requestModel: LoginRequestModel = new LoginRequestModel();
    requestModel.email = this.userName;
    requestModel.password = this.password;

    let requestCode: NoContentResponseModel = await this.idmService.loginRequest(requestModel)

    const intervalSub = interval(requestCode.delay)
      .subscribe(
        async () => {
          let responseCode: string = await this.gatewayService.reportResponse(requestCode.transactionID)
          if (responseCode !== "nocontent") {
            let responseCodeObj = new LoginResponseModel().deserialize(responseCode)
            if (responseCodeObj.resultCode === 220)
              alert(responseCodeObj.message);
            else
              alert(responseCodeObj.message);
            intervalSub.unsubscribe();
          }
          intervalSub.unsubscribe();
        }  
      )     
  }
}
