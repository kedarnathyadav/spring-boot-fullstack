import {Component} from '@angular/core';
import {AuthenticationRequest} from "../../models/authentication-request";
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  authenticationRequest: AuthenticationRequest = {};
  errorMsg: string = '';

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
  }


  login() {
    this.errorMsg = '';
    this.authenticationService.login(this.authenticationRequest)
      .subscribe({
        next: (authenticationResponse) => {
          // console.log(authenticationResponse);
          localStorage.setItem('user', JSON.stringify(authenticationResponse));
          this.router.navigate(['customers']);
        },
        error: (error) => {
          if (error.error.statusCode === 401) {
            this.errorMsg = 'Login and / or pass is incorrect';

          }
        }
      });
  }

  register() {
    this.router.navigate(['register']);
  }
}
