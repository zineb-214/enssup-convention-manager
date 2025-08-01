import { platformBrowser } from '@angular/platform-browser';
import { AppModule } from './app/app.module';
import { AuthService } from './app/Services/auth.service';

platformBrowser().bootstrapModule(AppModule, {
  ngZoneEventCoalescing: true, 
})
  .catch(err => console.error(err));
