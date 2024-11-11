import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {InputTextModule} from 'primeng/inputtext';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {CustomerComponent} from './components/customer/customer.component';
import {MenuBarComponent} from './components/menu-bar/menu-bar.component';
import {AvatarModule} from 'primeng/avatar';
import {AvatarGroupModule} from 'primeng/avatargroup';
import {MenuModule} from "primeng/menu";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MenuItemComponent} from './components/menu-item/menu-item.component';
import {HeaderBarComponent} from './components/header-bar/header-bar.component';
import {Button, ButtonDirective} from "primeng/button";
import {Ripple} from "primeng/ripple";
import {SidebarModule} from "primeng/sidebar";
import {ManageCustomerComponent} from './components/manage-customer/manage-customer.component';
import {LoginComponent} from './components/login/login.component';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {AuthenticationService} from "./services/authentication/authentication.service";
import {MessageModule} from "primeng/message";
import {JwtHelperService, JWT_OPTIONS} from '@auth0/angular-jwt';
import {HttpInterceptorService} from "./services/interceptor/http-interceptor.service";
import {CustomerCardComponent} from './components/customer-card/customer-card.component';
import {CardModule} from "primeng/card";
import {BadgeModule} from "primeng/badge";
import {ToastModule} from "primeng/toast";
import {ConfirmationService, MessageService} from "primeng/api";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import { RegisterComponent } from './components/register/register.component';

@NgModule({
  declarations: [
    AppComponent,
    CustomerComponent,
    MenuBarComponent,
    MenuItemComponent,
    HeaderBarComponent,
    ManageCustomerComponent,
    LoginComponent,
    CustomerCardComponent,
    RegisterComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CommonModule,
    InputTextModule,
    AvatarModule,
    AvatarGroupModule,
    MenuModule,
    BrowserAnimationsModule,
    Button,
    ButtonDirective,
    Ripple,
    SidebarModule,
    MessageModule,
    CardModule,
    BadgeModule,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [
    AuthenticationService,
    provideHttpClient(withInterceptorsFromDi()),
    {provide: JWT_OPTIONS, useValue: JWT_OPTIONS},
    JwtHelperService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpInterceptorService,
      multi: true,
    },
    MessageService,
    ConfirmationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
