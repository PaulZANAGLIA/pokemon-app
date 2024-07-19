import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { CookieService } from 'ngx-cookie-service';
import axios from 'axios'; // Importation statique d'axios
import { FormGroup } from '@angular/forms';

export type JwtTokenData = {
  sub: string;
  exp: number;
  email: string;
  username: string;
  roles: [string];
};

export type UserDto = {
  id: number;
  email: string;
  username: string;
  password: string;
  elo: number;
  roles: [string];
  friends: [number];
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private TOKEN_KEY = 'accessToken';

  private jwtHelper: JwtHelperService = new JwtHelperService();

  constructor(private cookieService: CookieService) {}

  public setTokenInCookies(token: string): void {
    this.cookieService.set(this.TOKEN_KEY, token);
  }

  private isTokenExpired(token: string) {
    return this.jwtHelper.isTokenExpired(token);
  }

  private getToken(): string | null {
    return this.cookieService.get(this.TOKEN_KEY) || null;
  }

  getTokenData(): JwtTokenData | undefined {
    const token = this.getToken();
    if (token) {
      const res = this.jwtHelper.decodeToken(token);
      if (res) return res;
    }
    return undefined;
  }

  isTokenValid(): boolean {
    let token = this.getToken();
    return token ? !this.isTokenExpired(token) : false;
  }

  PasswordMatchValidator(form: FormGroup) {
    const password = form.get('password')!.value;
    const passwordVerif = form.get('passwordVerif')!.value;

    return password === passwordVerif ? null : { passwordsDoNotMatch: true };
  }

  GetUser(dbUrl: string, id: number) {
    const config = {
      headers: {
        'Content-Type': 'application/json',
      },
    };

    return axios.get(dbUrl + `/${id}`, config);
  }

  UpdateUser(form: UserDto, dbUrl: string, id: number): Promise<void> {
    const data = JSON.stringify(form);
    let token = this.getToken();
    if (!token) token = 'invalid token';
    const config = {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
    };
    return axios.put(dbUrl + `/${id}`, data, config);
  }

  RegisterUser(form: FormGroup, dbUrl: string): Promise<void> {
    const data = JSON.stringify({
      email: form.get('email')!.value,
      username: form.get('username')!.value,
      password: form.get('password')!.value,
    });

    const config = {
      headers: {
        'Content-Type': 'application/json',
      },
    };
    return axios.post(dbUrl, data, config);
  }

  LoginUser(form: FormGroup, dbUrl: string) {
    let data = JSON.stringify({
      email: form.get('email')!.value,
      password: form.get('password')!.value,
    });

    const config = {
      headers: {
        'Content-Type': 'application/json',
      },
    };

    return axios.post(dbUrl, data, config);
  }
}
