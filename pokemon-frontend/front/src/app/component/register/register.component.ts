import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent implements OnInit {
  private DB_API_REGISTER_URL: string =
    'http://localhost:8080/api/auth/register';

  registerForm!: FormGroup;
  message: string = '';
  apiErrorMessage: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.registerForm = this.formBuilder.group(
      {
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.pattern(/.{6,}/)]],
        passwordVerif: ['', [Validators.required, Validators.pattern(/.{6,}/)]],
        username: [
          '',
          [
            Validators.required,
            Validators.minLength(3),
            Validators.maxLength(15),
          ],
        ],
      },
      {
        validators: [this.authService.PasswordMatchValidator],
      }
    );
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.message = 'Un ou plusieurs champs comportent des erreurs:';
      return;
    }
    this.authService
      .RegisterUser(this.registerForm, this.DB_API_REGISTER_URL)
      .then((r) => {
        console.log('Register done!', r);
        this.message = '';
        this.apiErrorMessage = '';
        this.router.navigate(['/auth/login']); // Redirect to default URL
      })
      .catch((e) => {
        console.error('Problem when register.', e);
        this.message = 'Un ou plusieurs champs comportent des erreurs: ';
        this.apiErrorMessage =
          "Can't register your account. Please check your email exist or retry later.";
      });
  }

  hasNonRequiredErrors(): boolean {
    const controls = this.registerForm.controls;
    for (const name in controls) {
      if (controls[name].errors) {
        const errors = controls[name].errors!;
        if (!(Object.keys(errors).length === 1 && errors['required'])) {
          return true;
        }
      }
    }
    if (this.registerForm.errors?.['passwordsDoNotMatch']) return true;
    this.message = '';
    return false;
  }
}
