import { Component } from '@angular/core';
import { AuthService } from '../../../Services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-template',
  standalone: false,
  templateUrl: './admin-template.component.html',
  styleUrl: './admin-template.component.css'
})
export class AdminTemplateComponent {
constructor(public authService:AuthService, private router:Router){}

 logout() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
