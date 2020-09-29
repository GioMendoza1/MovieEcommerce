import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { GatewayService } from './gateway.service';
import { RouterTestingModule } from '@angular/router/testing';
import { environment } from 'src/environments/environment';

fdescribe('GatewayService', () => {
  let service: GatewayService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [GatewayService]
    });
    service = TestBed.get(GatewayService);
    httpMock = TestBed.get(HttpTestingController);
  });

  //Verify for no outstanding requests
  afterEach(() => {
    httpMock.verify();
  });

  describe('#reportResponse', () => {
    it('should send a http.get request and return a JSON string', fakeAsync (() => {
      let transactionID: string = "1234";
      let dummyJsonResponse: string = '{"name": "test", "number": 123}';

      //Create request via function call 
      service.reportResponse(transactionID).then((response: string) => {
        //Check returned data
        expect(response).not.toEqual(null);
        //Convert JSON string to object and store keys into string array
        let keysFromObject = Object.keys(JSON.parse(response));
        expect(keysFromObject).toContain("name");
      })
      //Short delay for request to send
      tick(1000);

      //Store request from function call
      const req = httpMock.expectOne(r=>r.url === `${environment.apiGatewayURL}/api/g/report`, 'Request not found')
      expect(req.request.method).toBe("GET");
      //Verifying transactionID header
      expect((req.request.headers.get('transactionID'))).toEqual("1234");
      //Request return value
      req.flush(dummyJsonResponse);
    }));
  });
});
