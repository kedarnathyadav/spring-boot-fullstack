import {Component} from '@angular/core';
import {MenuItem, MenuItemCommandEvent} from "primeng/api";
import {AuthenticationResponse} from "../../models/authentication-response";
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header-bar',
  templateUrl: './header-bar.component.html',
  styleUrl: './header-bar.component.scss'
})
export class HeaderBarComponent {

  constructor(
    private router: Router,
  ) {
  }

  items: MenuItem[] = [
    {label: 'Profile', icon: 'pi pi-user'},
    {label: 'Settings', icon: 'pi pi-cog'},
    {separator: true},
    {
      label: 'Sign out',
      icon: 'pi pi-sign-out',
      command: () => {
        localStorage.clear();
        this.router.navigate(['login']);
      }
    }
  ];

  storedUser = localStorage.getItem('user');

  get userName(): string {

    if (this.storedUser) {
      const authResponse: AuthenticationResponse = JSON.parse(this.storedUser);
      if (authResponse && authResponse.customerDTO && authResponse.customerDTO.username) {
        return authResponse.customerDTO.username;
      }
    }
    return '--';
  }

  get userRole(): string {
    if (this.storedUser) {
      const authResponse: AuthenticationResponse = JSON.parse(this.storedUser);
      if (authResponse && authResponse.customerDTO && authResponse.customerDTO.roles) {
        return authResponse.customerDTO.roles[0];
      }
    }
    return '--';
  }

}
