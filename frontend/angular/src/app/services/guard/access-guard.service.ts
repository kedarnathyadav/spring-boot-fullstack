import {Injectable} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import {AuthenticationResponse} from '../../models/authentication-response';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AccessGuardService implements CanActivate {

  constructor(
    private router: Router,
    private jwtHelper: JwtHelperService
  ) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      const token = authResponse.token;
      if (token) {
        const isTokenNonExpired = !this.jwtHelper.isTokenExpired(token);
        if (isTokenNonExpired) {
          return true;
        }
      }
    }
    return this.router.createUrlTree(['login']);
  }
}
