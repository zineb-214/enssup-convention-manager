import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authDeletedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  // Décoder le JWT
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const isDeleted = payload.deleted;

   

    if (isDeleted === true) {
       alert('⛔ Votre compte a été désactivé. Veuillez contacter l\'administrateur.');
      router.navigate(['/login']);
      return false;
    }
      
return true;
 

    

  } catch (e) {
    console.error('Erreur lors du décodage du token', e);
    router.navigate(['/login']);
    return false;
  }
};
