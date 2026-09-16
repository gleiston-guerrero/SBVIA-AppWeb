import { Component, OnInit, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, ReactiveFormsModule],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css'
})
export class ShellComponent implements OnInit {
  user: any = null;
  sidebarAbierto = true;
  menuAbierto = false;
  modalPerfilAbierto = false;
  perfilForm: FormGroup;
  guardandoPerfil = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.perfilForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.maxLength(255)]],
      lastName: ['', [Validators.required, Validators.maxLength(255)]],
      phone: ['', [Validators.maxLength(20)]]
    });

    // Actualizar user reactivamente si cambia en el AuthService
    effect(() => {
      this.user = this.authService.currentUser();
    });
  }

  ngOnInit(): void {
    // Inicialización si es necesaria
  }

  toggleSidebar(): void {
    this.sidebarAbierto = !this.sidebarAbierto;
  }

  toggleMenu(): void {
    this.menuAbierto = !this.menuAbierto;
  }

  abrirModalPerfil(): void {
    this.menuAbierto = false; // Cerrar el menú
    if (this.user) {
      this.perfilForm.patchValue({
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        phone: this.user.phone
      });
    }
    this.modalPerfilAbierto = true;
  }

  cerrarModalPerfil(): void {
    this.modalPerfilAbierto = false;
  }

  guardarPerfil(): void {
    if (this.perfilForm.invalid) return;

    this.guardandoPerfil = true;
    this.authService.actualizarPerfil(this.perfilForm.value).subscribe({
      next: () => {
        this.guardandoPerfil = false;
        this.cerrarModalPerfil();
      },
      error: (err: any) => {
        console.error('Error al actualizar perfil', err);
        this.guardandoPerfil = false;
        alert('Ocurrió un error al guardar el perfil.');
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
