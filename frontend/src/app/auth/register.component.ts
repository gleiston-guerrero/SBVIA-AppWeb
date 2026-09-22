import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';
import { TranslatePipe } from '../i18n/translate.pipe';
import { LangToggleComponent } from '../i18n/lang-toggle.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TranslatePipe, LangToggleComponent],
  templateUrl: './register.component.html',
  styleUrl: './login.component.css' // Reutilizamos estilos del login
})
export class RegisterComponent {
  data = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: ''
  };
  /** The error KEY is stored, not its text, so it is re-translated when the language changes. */
  errorKey = '';
  loading = false;

  // State for the successful-registration card showing the generated username
  registered = false;
  registeredUsername = '';
  registeredEmail = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.data.firstName || !this.data.lastName || !this.data.email || !this.data.password) {
      this.errorKey = 'register.errorRequired';
      return;
    }

    this.loading = true;
    this.errorKey = '';

    this.authService.register(this.data).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.registered = true;
        this.registeredUsername = res?.user?.username || '';
        this.registeredEmail = res?.user?.email || this.data.email;
      },
      error: (err: any) => {
        this.loading = false;
        if (err.status === 409) {
          this.errorKey = 'register.errorEmailTaken';
        } else if (err.status === 400) {
          this.errorKey = 'register.errorInvalid';
        } else {
          this.errorKey = 'login.errorServer';
        }
      }
    });
  }
}
