import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { fakeAsync,  tick } from '@angular/core/testing';
import { IdmService } from './idm.service';
import { RouterTestingModule } from '@angular/router/testing';
import { NoContentResponseModel } from 'src/app/models/no-content-response-model';
import { environment } from 'src/environments/environment';
import { LoginRequestModel } from 'src/app/models/idm/login-request-model';
import { RegisterRequestModel } from 'src/app/models/idm/register-request-model';

fdescribe('IdmService', () => {
  let service: IdmService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [IdmService]
    });
    service = TestBed.get(IdmService);
    httpMock = TestBed.get(HttpTestingController);
  });

  //Verify for no outstanding requests
  afterEach(() => {
    httpMock.verify();
  });

  describe('#loginRequest', () => {
    it('should send a http.post request and return a NoContentResponseModel', fakeAsync (() => {
      let dummyNoContentData: NoContentResponseModel = new NoContentResponseModel();
      dummyNoContentData.transactionID = "1234";
      dummyNoContentData.delay = 1;
      dummyNoContentData.message = "test";

      //Create request via function call 
      service.loginRequest(new LoginRequestModel()).then((noContentReturn: NoContentResponseModel) => {
        //Check returned data
        expect(noContentReturn).toEqual(dummyNoContentData);
        expect(noContentReturn.transactionID).toEqual("1234");
      })
      tick(1000);

      //Store request from function call
      const req = httpMock.expectOne(`${environment.apiGatewayURL}/api/g/idm/login`, 'Request not found')
      expect(req.request.method).toBe("POST");
      //Request return value
      req.flush(dummyNoContentData);
    }));
  });

  describe('#registerRequest', () => {
    it('should send a http.post request and return a NoContentResponseModel', fakeAsync (() => {
      let dummyNoContentData: NoContentResponseModel = new NoContentResponseModel();
      dummyNoContentData.transactionID = "1234";
      dummyNoContentData.delay = 1;
      dummyNoContentData.message = "test";

      //Create request via function call 
      service.registerRequest(new RegisterRequestModel()).then((noContentReturn: NoContentResponseModel) => {
        //Check returned data
        expect(noContentReturn).toEqual(dummyNoContentData);
        expect(noContentReturn.transactionID).toEqual("1234");
      })
      tick(1000);

      //Store request from function call
      const req = httpMock.expectOne(`${environment.apiGatewayURL}/api/g/idm/register`, 'Request not found')
      expect(req.request.method).toBe("POST");
      //Request return value
      req.flush(dummyNoContentData);
    }));
  });




});
