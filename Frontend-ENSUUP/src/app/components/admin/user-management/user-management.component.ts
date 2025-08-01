import { Component, OnInit } from '@angular/core';
import {AppPermission, AppRole, AppUser, PermissionRequest, UserRole, UserServiceService } from '../../../Services/user-service.service';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css'],
  standalone:false
})
export class UserManagementComponent implements OnInit {
  today: string = new Date().toISOString().split('T')[0];
  showAddForm = false;
  users: AppUser[] = [];
  roles: AppRole[] = [];
 newUser: AppUser = { 
  username: '', 
  password: '', 
  email: '', 
  permissions: []  // permissions est un tableau vide au départ
};


  selectedUser: AppUser | undefined;
  selectedPermission: AppPermission = {
    username: '',
    canRead: false,
    canCreate: false,
    canDelete: false,
    canUpdate: false
  };
  userRole: UserRole = { username: '', roleName: '' };
  message = '';

  // Filtres
  usernameFilter: string = '';
  isDeletedFilter: boolean | null = null;
  startDateFilter: string = '';
  endDateFilter: string = '';

  // Pagination
  page: number = 0;
  size: number = 3;
  totalPages: number = 0;

  // Permissions
  permissionRequest: PermissionRequest = this.resetPermissionRequest();

  loading: boolean = false;
  constructor(private userService: UserServiceService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadRoles();
  }

  resetPermissionRequest(): PermissionRequest {
    return {
      username: '',
      canCreate: false,
      canRead: false,
      canUpdate: false,
      canDelete: false
    };
  }

  loadRoles() {
    this.userService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des rôles', err);
      }
    });
  }

loadUsers() {
  this.loading = true;
  const isDeleted = this.isDeletedFilter ?? undefined;

  this.userService
    .filterUsers(
      this.usernameFilter,
      isDeleted,
      this.startDateFilter,
      this.endDateFilter,
      this.page,
      this.size
    )
    .subscribe({
      next: (data) => {
        this.users = data.content;
        this.totalPages = data.totalPages;
        this.loading = false;
      },
      error: (err) => {
        this.message = 'Erreur lors du chargement des utilisateurs.';
        console.error(err);
        this.loading = false;
      }
    });
}

onCanReadChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const isChecked = input.checked;

  if (!isChecked && (this.permissionRequest.canUpdate || this.permissionRequest.canDelete)) {
    this.permissionRequest.canRead = true; // Remet à true
  } else {
    this.permissionRequest.canRead = isChecked;
  }
}
onCanReadEditChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const isChecked = input.checked;

  if (!isChecked && (this.selectedPermission.canUpdate || this.selectedPermission.canDelete)) {
    this.selectedPermission.canRead = true;
  } else {
    this.selectedPermission.canRead = isChecked;
  }
}
onEditPermissionChange() {
  if (this.selectedPermission.canUpdate || this.selectedPermission.canDelete) {
    this.selectedPermission.canRead = true;
  }}
 addUser() {
  this.userService.addUser(this.newUser).subscribe(user => {
    this.message = `Utilisateur ${user.username} ajouté`;
    this.permissionRequest.username = user.username;

    this.userService.addPermissionsToUser(this.permissionRequest).subscribe(() => {
      this.message += ' avec permissions.';
      this.newUser = { username: '', password: '', email: '', permissions: [] };
      this.permissionRequest = this.resetPermissionRequest();
      this.loadUsers();
      this.showAddForm = false;
    });
  });
}


  selectUser(user: AppUser) {
  this.selectedUser = { ...user };

  if (user.permissions && user.permissions.length > 0) {
    this.selectedPermission = { ...user.permissions[0] };
  } else {
    this.selectedPermission = {
      username: user.username,
      canCreate: false,
      canRead: false,
      canUpdate: false,
      canDelete: false
    };
    this.selectedUser.permissions = [this.selectedPermission];
  }
}


  updateUser() {
  if (!this.selectedUser || !this.selectedUser.id) return;

  // Transforme la permission unique en tableau (collection)
  this.selectedUser.permissions = [this.selectedPermission];

  this.userService.updateUser(this.selectedUser.id, this.selectedUser).subscribe({
    next: (user) => {
      this.message = `Utilisateur ${user.username} mis à jour`;
      this.selectedUser = undefined;
      this.selectedPermission = this.resetPermissionRequest();
      this.loadUsers();
    },
    error: (err) => {
      console.error('Erreur lors de la mise à jour :', err);
      this.message = 'Erreur lors de la mise à jour de l’utilisateur.';
    }
  });
}


  onDeleteUser(id: number) {
    this.userService.softDeleteUser(id).subscribe(() => {
      this.message = 'Utilisateur supprimé avec succès.';
      this.loadUsers();
    });
  }

  toggleUserStatus(user: AppUser) {
    if (!user.id) return;
    const updatedStatus = !user.deleted;
    user.deleted = updatedStatus;

    const request$ = updatedStatus
      ? this.userService.softDeleteUser(user.id)
      : this.userService.restoreUser(user.id);

    request$.subscribe({
      next: () => {
        this.message = `Utilisateur ${updatedStatus ? 'supprimé' : 'restauré'}`;
        this.loadUsers();
      },
      error: (err) => {
        console.error(err);
        user.deleted = !updatedStatus;
      }
    });
  }

  addPermissionsToUser() {
    this.userService.addPermissionsToUser(this.permissionRequest).subscribe(msg => {
      this.message = msg;
      this.permissionRequest = this.resetPermissionRequest();
      this.loadUsers();
    });
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadUsers();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadUsers();
    }
  }

  onFilter(): void {
    this.page = 0;
    this.loadUsers();
  }
 
}