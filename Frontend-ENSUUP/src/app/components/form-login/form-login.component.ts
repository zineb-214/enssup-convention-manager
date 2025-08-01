import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../Services/auth.service';
import { Route, Router } from '@angular/router';

@Component({
  selector: 'app-form-login',
  standalone: false,
  templateUrl: './form-login.component.html',
  styleUrls: ['./form-login.component.css'] 
})
export class FormLoginComponent implements OnInit {
  loginForm!: FormGroup;
  showPassword = false;
  loginError = false;
  logoPath = '/images/logo.png';

  constructor(
    private fb: FormBuilder,
    private authservice: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    const { username, password } = this.loginForm.value;

    this.authservice.login(username, password).subscribe({
      next: data => {
        this.loginError = false;
        this.authservice.profile(data);

        const roles = this.authservice.getUserRoles();
        this.authservice.loadPermissions().subscribe(() => {
          if (roles.includes("ADMIN")) {
            this.router.navigateByUrl("/admin");
          } else if (roles.includes("USER")) {
            this.router.navigateByUrl("/user");
          } else {
            this.router.navigateByUrl("/login");
          }
        });
      },
      error: err => {
        console.error(err);
        this.loginError = true;
      }
    });
  }
}