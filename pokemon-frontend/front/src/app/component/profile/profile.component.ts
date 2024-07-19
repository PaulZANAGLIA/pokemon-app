import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  AuthService,
  JwtTokenData,
  UserDto,
} from '../../service/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  isLoading = true;
  isEditing = false;

  user: JwtTokenData | undefined;

  userForm!: FormGroup;
  message: string = '';
  DB_API_UPDATE_USER_URL: string = 'http://localhost:8080/api/users';
  apiErrorMessage: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  toggleEdit() {
    this.isEditing = !this.isEditing;
  }

  updateProfile() {
    if (this.userForm.invalid) {
      this.message = 'Un ou plusieurs champs comportent des erreurs:';
      return;
    }

    this.isLoading = true;

    let userId = +(this.user?.sub ?? '-1');
    this.authService
      .GetUser(this.DB_API_UPDATE_USER_URL, userId)
      .then((res) => {
        const userInformation: UserDto = res.data;

        const userDto: UserDto = {
          id: userId,
          email: this.userForm.get('email')?.value ?? userInformation.email,
          username:
            this.userForm.get('username')?.value ?? userInformation.username,
          password: this.userForm.get('password')?.value ?? '',
          elo: userInformation.elo,
          roles: this.user?.roles ?? userInformation.roles,
          friends: userInformation.friends,
        };

        this.authService
          .UpdateUser(userDto, this.DB_API_UPDATE_USER_URL, userId)
          .then((r) => {
            console.log('Update done!', r);
            this.message = '';
            this.apiErrorMessage = '';
            // TODO: logout to force login again to update data ??
            this.toggleEdit();
          })
          .catch((e) => {
            console.error('Problem when update.', e);
            this.user = this.authService.getTokenData();
            this.message = 'Un ou plusieurs champs comportent des erreurs: ';
            this.apiErrorMessage =
              "Can't update your account. Please check your email exist or retry later.";
          });
      })
      .catch((e) => {
        console.error('Problem when retrieving user.', e);
        this.message = 'Un ou plusieurs champs comportent des erreurs: ';
        this.apiErrorMessage =
          "Can't retrieve data of your account. Retry later.";
        this.user = this.authService.getTokenData();
      })
      .finally(() => (this.isLoading = false));
  }

  ngOnInit() {
    if (!this.authService.isTokenValid()) this.router.navigate(['/auth/login']);

    new Promise((res) => res(this.authService.getTokenData()))
      .then((res) => (this.user = res as JwtTokenData | undefined))
      .finally(() => {
        this.userForm = this.formBuilder.group(
          {
            email: [this.user?.email ?? '', [Validators.email]],
            password: ['', [Validators.pattern(/.{6,}/)]],
            passwordVerif: ['', [Validators.pattern(/.{6,}/)]],
            username: [
              this.user?.username ?? '',
              [Validators.minLength(3), Validators.maxLength(15)],
            ],
          },
          {
            validators: [this.authService.PasswordMatchValidator],
          }
        );
        this.isLoading = false;
      });
  }

  hasUserUpdateFormErrors(): boolean {
    const controls = this.userForm.controls;
    for (const name in controls) {
      if (controls[name].errors) {
        const errors = controls[name].errors!;
        console.log(
          'name:',
          name,
          Object.keys(errors).length,
          this.userForm.invalid
        );
        if (Object.keys(errors).length > 0) {
          return true;
        }
      }
    }
    if (this.userForm.errors?.['passwordsDoNotMatch']) return true;
    this.message = '';
    return false;
  }
}
