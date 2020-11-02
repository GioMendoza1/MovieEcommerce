import { Component, ViewChild, AfterViewInit, OnInit, Injectable} from '@angular/core';
import { MatInput } from '@angular/material/input';
import {
  HttpClient
} from '@angular/common/http';
import { interval, Subscription } from 'rxjs';
import { IdmService } from 'src/app/services/idm/idm.service';
import { RegisterRequestModel } from 'src/app/models/idm/register-request-model';
import { RegisterResponseModel } from 'src/app/models/idm/register-response-model';
import { NoContentResponseModel } from 'src/app/models/no-content-response-model';
import { GatewayService } from 'src/app/services/gateway/gateway.service';
import { FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})

export class RegisterComponent implements OnInit, AfterViewInit {

  constructor( 
    private idmService: IdmService,
    private gatewayService: GatewayService,
    private router: Router
  ){}

  public userName: string = "";
  public password: string = "";
  public passwordAgain: string = "";

  public responseMessage: string = "";
  public responseReturned: boolean = false;
  public responseErrorReturned: boolean = false;

  public userNameControl = new FormControl('', [Validators.required, Validators.email]);
  public passwordControl = new FormControl('', [Validators.required]);
  public passwordAgainControl = new FormControl('', [Validators.required]);

  private intervalSub: Subscription;

  ngOnInit() {
  }

  ngAfterViewInit() {   
  }

  ngOnDestroy(){
    this.intervalSub.unsubscribe();
  }

  public getErrorMessage(control: FormControl) {
    if (control.hasError('required')) {
      return 'You must enter a value';
    }
    return control.hasError('email') ? 'Not a valid email' : '';
  }

  /*On button click if passwords entered match then send request, otherwise display error*/
  public onClick(): void {
    if (this.password === this.passwordAgain)
      this.sendRequest();
    else{
      this.responseErrorReturned = true;
      this.responseMessage = "Error: Passwords don't match";
    }  
  }

  /*Send registration request to API gateway and if successful navigate to login page, otherwise display error*/
  private async sendRequest(): Promise<void> {
    let requestModel: RegisterRequestModel = new RegisterRequestModel();
    requestModel.email = this.userName;
    requestModel.password = this.password;

    let requestCode: NoContentResponseModel = await this.idmService.registerRequest(requestModel)
    let successfulReg: boolean = false;

    this.intervalSub = interval(requestCode.delay)
      .subscribe(
        async () => {
          let responseCode: string = await this.gatewayService.reportResponse(requestCode.transactionID)
          if (responseCode !== "nocontent") {
            let responseCodeObj: RegisterResponseModel = new RegisterResponseModel().deserialize(responseCode)
            successfulReg = this.checkResponse(responseCodeObj)
            //If registration is successful, go to login page
            if (successfulReg)
              this.router.navigate(['login']);
            else
              this.intervalSub.unsubscribe();
          }
        }  
      )
  }

  private checkResponse(response: RegisterResponseModel): boolean {
    this.responseMessage = "";
    if (response.resultCode === 110){
      this.responseReturned = true;
      if (this.responseErrorReturned)
        this.responseErrorReturned = false;
      this.responseMessage = response.message;
      
      return true;
    }
    else{
      this.responseErrorReturned = true;
      this.responseMessage = "Error: " + response.message;

      return false;
    }
  }

}
