import { Component, AfterViewInit, OnInit} from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { IdmService } from 'src/app/services/idm/idm.service';
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

  public responseMessage: string = "";
  public responseReturned: boolean = false;
  public responseErrorReturned: boolean = false;

  public userNameControl = new FormControl('', [Validators.required, Validators.email]);
  public passwordControl = new FormControl('', [Validators.required]);

  private intervalSub: Subscription;


  ngOnInit() {
  }

  ngAfterViewInit() {
    console.log('Values on ngAfterViewInit():');    
  }

  ngOnDestroy() {
    this.intervalSub.unsubscribe();
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

    let requestCode: NoContentResponseModel = await this.idmService.loginRequest(requestModel);
    let successfulLogin: boolean = false;

    this.intervalSub = interval(requestCode.delay)
      .subscribe(
        async () => {
          let responseCode: string = await this.gatewayService.reportResponse(requestCode.transactionID)
          if (responseCode !== "nocontent") {
            let responseCodeObj: LoginResponseModel = new LoginResponseModel().deserialize(responseCode);
            successfulLogin = this.checkResponse(responseCodeObj);
            this.intervalSub.unsubscribe();
          }
        }  
      )
  }

  private checkResponse(response: LoginResponseModel): boolean {
    this.responseMessage = "";
    if (response.resultCode === 120){
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
