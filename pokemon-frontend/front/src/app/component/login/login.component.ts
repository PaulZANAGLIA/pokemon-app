import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService } from '../../service/auth/auth.service';
import { CommonModule } from '@angular/common';

type LoginResp = {
  accessToken: string;
};

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterOutlet, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  public registerPath = '/auth/register';
  private DB_API_LOGIN_URL: string = 'http://localhost:8080/api/auth/login';

  loginForm!: FormGroup;
  message: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.authService
      .LoginUser(this.loginForm, this.DB_API_LOGIN_URL)
      .then((r) => {
        let data: LoginResp = r.data as LoginResp;
        this.authService.setTokenInCookies(data.accessToken);
        this.message = '';
        this.router.navigate(['/']);
      })
      .catch((e) => {
        if (e.response.status) {
          this.message = 'Bad Login or bad password given. Please retry.';
        }
      });
  }
}
