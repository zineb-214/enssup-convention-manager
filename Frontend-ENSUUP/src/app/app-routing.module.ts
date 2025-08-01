import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { AdminTemplateComponent } from './components/admin/admin-template/admin-template.component';
import { authenticationGuard } from './guards/authentication.guard';
import { UserManagementComponent } from './components/admin/user-management/user-management.component';
import { ConventionTypeComponent } from './components/admin/convention-type/convention-type.component';
import { UserTemplateComponent } from './components/user/user-template/user-template.component';
import { AddConventionComponent } from './components/user/add-convention/add-convention.component';
import { ConventionsComponent } from './components/user/conventions/conventions.component';
import { permissionGuardGuard } from './guards/permission-guard.guard';
import { UserHomeComponent } from './components/user/user-home/user-home.component';
import { authDeletedGuard } from './guards/auth-deleted.guard';

  const routes: Routes = [
    { path: "login", component: FormLoginComponent },
    { path: "", redirectTo: "/login", pathMatch: "full" },
    {
      path: "admin",component: AdminTemplateComponent ,canActivate :[authenticationGuard],
     children: [
       { path: '', redirectTo: 'usermanagement', pathMatch: 'full' },
        { path: "usermanagement", component: UserManagementComponent },
        { path: "conventiontypes", component: ConventionTypeComponent}
      ]
    },
  {
  path: "user",
  component: UserTemplateComponent,
  canActivate: [authDeletedGuard,authenticationGuard], 
  children: [
     { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component:UserHomeComponent },
    {
      path: "conventions",
      component: ConventionsComponent,
      canActivate: [permissionGuardGuard],
      data: { permission: 'canRead' }
    },
    {
      path: "addConvention",
      component: AddConventionComponent,
      canActivate: [permissionGuardGuard],
      data: { permission: 'canCreate' }
    }
  ]
}

  ];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
