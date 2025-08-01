import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../Services/auth.service';
import { PermissionRequest } from '../Services/user-service.service';
import { inject } from '@angular/core';
import { map, take } from 'rxjs';

export const permissionGuardGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const requiredPermission = route.data['permission'] as keyof PermissionRequest | undefined;

  if (!requiredPermission) {
    return true;
  }

  return authService.getUserPermissionsAsync().pipe(
    take(1),
    map(userPermissions => {
      const hasPermission = !!(userPermissions && userPermissions[requiredPermission]);
      if (hasPermission) {
        return true;
      } else {
        return false;
      }
    })
  );
};