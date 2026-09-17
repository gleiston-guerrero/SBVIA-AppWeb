import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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
  errorMessage = '';
  loading = false;

  // Estado para show confirmación de registro exitoso con el nombre de user generado
  registered = false;
  registeredUsername = '';
  registeredEmail = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.data.firstName || !this.data.lastName || !this.data.email || !this.data.password) {
      this.errorMessage = 'Todos los campos son obligatorios';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.registro(this.data).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.registered = true;
        this.registeredUsername = res?.user?.username || '';
        this.registeredEmail = res?.user?.email || this.data.email;
      },
      error: (err: any) => {
        this.loading = false;
        if (err.status === 409) {
          this.errorMessage = 'El email ya está registrado';
        } else if (err.status === 400) {
          this.errorMessage = 'Datos inválidos. Verifique el formato.';
        } else {
          this.errorMessage = 'Error en el servidor. Intente más tarde.';
        }
      }
    });
  }
}
