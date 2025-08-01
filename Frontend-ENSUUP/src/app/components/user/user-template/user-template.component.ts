import { Component, OnInit } from '@angular/core';
import { AuthService, PermissionRequest } from '../../../Services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-template',
  standalone: false,
  templateUrl: './user-template.component.html',
  styleUrl: './user-template.component.css'
})
export class UserTemplateComponent implements OnInit {
  permissions: PermissionRequest | null = null;
constructor(public authService:AuthService, private router:Router){}

 logout() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }

  userPermissions: PermissionRequest | null = null;

  ngOnInit(): void {
    this.authService.getUserPermissionsAsync().subscribe({
      next: (perms) => {
        this.permissions = perms;
        console.log('Permissions utilisateur chargées :', this.permissions);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des permissions', err);
      }
    });
  }
}
