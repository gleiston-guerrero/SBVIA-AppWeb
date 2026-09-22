import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';
import { TranslatePipe } from '../i18n/translate.pipe';
import { LangToggleComponent } from '../i18n/lang-toggle.component';
import { LanguageService } from '../i18n/language.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TranslatePipe, LangToggleComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  credentials = { identificador: '', password: '' };
  /** The error KEY is stored, not its text, so it is re-translated when the language changes. */
  errorKey = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private i18n: LanguageService
  ) {}

  onSubmit() {
    if (!this.credentials.identificador || !this.credentials.password) {
      this.errorKey = 'login.errorRequired';
      return;
    }

    this.loading = true;
    this.errorKey = '';

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err: any) => {
        this.loading = false;
        this.errorKey = err.status === 401 ? 'login.errorCredentials' : 'login.errorServer';
      }
    });
  }
}
