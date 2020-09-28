import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})

export class GatewayService {

  constructor(private http: HttpClient, private router: Router) { }

  public reportResponse(transactionID: string) : Promise<string> {
    return this.http.get<string>(`${environment.apiGatewayURL}/api/g/report`, { headers: {'Content-Type': 'application/json',  'transactionID': transactionID } } ).toPromise()
  }
}
