import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule  } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { FormsModule } from '@angular/forms';
import {ReactiveFormsModule } from '@angular/forms';
import { AdminTemplateComponent } from './components/admin/admin-template/admin-template.component';
import { AuthInterceptor } from './Interceptors/auth-http.interceptor';
import { UserManagementComponent } from './components/admin/user-management/user-management.component';
import { RouterModule } from '@angular/router';
import { ConventionTypeComponent } from './components/admin/convention-type/convention-type.component';
import { UserTemplateComponent } from './components/user/user-template/user-template.component';
import { AddConventionComponent } from './components/user/add-convention/add-convention.component';
import { ConventionsComponent } from './components/user/conventions/conventions.component';
import { UserHomeComponent } from './components/user/user-home/user-home.component';

@NgModule({
  declarations: [
    AppComponent,
    FormLoginComponent,
    AdminTemplateComponent,
    UserManagementComponent,
    ConventionTypeComponent,
    UserTemplateComponent,
    AddConventionComponent,
    ConventionsComponent,
    UserHomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule
  ],
  providers: [
   {
    provide:HTTP_INTERCEPTORS,
    useClass:AuthInterceptor,
    multi:true
   }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
