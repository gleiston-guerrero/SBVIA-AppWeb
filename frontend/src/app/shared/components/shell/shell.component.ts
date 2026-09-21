import { Component, OnInit, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../auth/auth.service';
import { TranslatePipe } from '../../../i18n/translate.pipe';
import { LangToggleComponent } from '../../../i18n/lang-toggle.component';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, ReactiveFormsModule,
            TranslatePipe, LangToggleComponent],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css'
})
export class ShellComponent implements OnInit {
  user: any = null;
  sidebarAbierto = true;
  menuAbierto = false;
  modalPerfilAbierto = false;
  profileForm: FormGroup;
  guardandoPerfil = false;
  profileError = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.profileForm = this.fb.group({
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
      this.profileForm.patchValue({
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        phone: this.user.phone
      });
    }
    this.profileError = '';
    this.modalPerfilAbierto = true;
  }

  cerrarModalPerfil(): void {
    this.modalPerfilAbierto = false;
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;

    this.guardandoPerfil = true;
    this.authService.updateProfile(this.profileForm.value).subscribe({
      next: () => {
        this.guardandoPerfil = false;
        this.cerrarModalPerfil();
      },
      error: (err: any) => {
        console.error('Error al update perfil', err);
        this.guardandoPerfil = false;
        this.profileError = 'Ocurrió un error al save el perfil.';
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
