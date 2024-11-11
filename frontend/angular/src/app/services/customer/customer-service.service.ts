import {Injectable} from '@angular/core';
import {AuthenticationRequest} from "../../models/authentication-request";
import {Observable} from "rxjs";
import {AuthenticationResponse} from "../../models/authentication-response";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {CustomerDTO} from "../../models/customer-dto";
import {environment} from "../../../environments/environment.development";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {CustomerUpdateRequest} from "../../models/customer-update-request";

@Injectable({
  providedIn: 'root'
})
export class CustomerServiceService {

  private readonly customerUrl = `${environment.api.baseUrl}/${environment.api.customerUrl}`;


  constructor(private http: HttpClient) {
  }

  findAll(): Observable<Array<CustomerDTO>> {
    return this.http.get<Array<CustomerDTO>>(this.customerUrl);
  }

  registerCustomer(customer: CustomerRegistrationRequest): Observable<void> {
    return this.http.post<void>(this.customerUrl, customer);

  }

  deleteCustomer(id: number | undefined): Observable<void> {
    return this.http.delete<void>(`${this.customerUrl}/${id}`);
  }

  updateCustomer(id: number | undefined, customer: CustomerUpdateRequest): Observable<void> {
    return this.http.put<void>(`${this.customerUrl}/${id}`, customer);
  }


}
