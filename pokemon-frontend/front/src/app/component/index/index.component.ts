import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../service/auth/auth.service';
import {
  Router,
  RouterLink,
  RouterModule,
  RouterOutlet,
} from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-index',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterLink, RouterOutlet],
  templateUrl: './index.component.html',
  styleUrl: './index.component.css',
})
export class IndexComponent implements OnInit {
  indexPath = {
    home: '',
    profile: '/profile',
    teams: '/teams',
    searchTrainers: '/search-trainers',
    logout: '/logout',
  };

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (!this.authService.isTokenValid()) this.router.navigate(['/auth/login']);
    return;
  }
}
