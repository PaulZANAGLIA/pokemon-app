import { Routes } from '@angular/router';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { IndexComponent } from './component/index/index.component';
import { ProfileComponent } from './component/profile/profile.component';
import { PageNotFoundComponent } from './component/page-not-found/page-not-found.component';

export const routes: Routes = [
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  {
    path: '',
    component: IndexComponent,
    children: [{ path: 'profile', component: ProfileComponent }],
  },
  { path: '**', component: PageNotFoundComponent }, // Wildcard route for a 404 page
];
