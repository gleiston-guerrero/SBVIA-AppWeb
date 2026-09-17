import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UsuarioService, User } from './user.service';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../shared/components/toast/toast.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  page = 0;
  size = 10;
  totalPages = 0;
  rolesDisponibles = ['PARTICIPANTE', 'INSTRUCTOR', 'ADMINISTRADOR'];

  get usuariosActivos(): number {
    return this.users.filter(user => !user.accountLocked).length;
  }

  // Modal de edición
  showModal = false;
  editingUser: Partial<User> = {};
  isSaving = false;

  // Modal de Confirmación
  showConfirmation = false;
  mensajeConfirmacion = '';
  accionConfirmacion: () => void = () => {};

  constructor(
    private userService: UsuarioService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.list(this.page, this.size).subscribe({
      next: (data: any) => {
        this.users = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err: any) => {
        console.error('Error al load users', err);
      }
    });
  }

  cambiarPagina(newPage: number): void {
    this.page = newPage;
    this.loadUsers();
  }

  cambiarRol(user: User, nuevoRol: string): void {
    if (user.role === nuevoRol) return;
    
    this.abrirConfirmacion(
      `¿Estás seguro de cambiar el role de ${user.firstName} a ${nuevoRol}?`,
      () => {
        if (user.id !== undefined) {
          this.userService.cambiarRol(user.id, nuevoRol).subscribe({
            next: () => {
              this.toastService.showSuccess('Role actualizado exitosamente');
              this.loadUsers();
            },
            error: (err: any) => {
              console.error('Error actualizando role', err);
              const errMsg = err.error?.detail || err.error?.message || 'No se pudo update el role';
              this.toastService.showError(errMsg);
              this.loadUsers();
            }
          });
        }
      },
      () => {
        this.loadUsers(); // revert select visual state if cancelled
      }
    );
  }

  desactivarUsuario(id: number | undefined, nombre: string): void {
    if (id !== undefined) {
      this.abrirConfirmacion(
        `¿Estás seguro de desactivar la cuenta de ${nombre}?`,
        () => {
          this.userService.delete(id).subscribe({
            next: () => {
              this.toastService.showSuccess('User desactivado exitosamente');
              this.loadUsers();
            },
            error: (err: any) => {
              console.error('Error al desactivar user', err);
              const errMsg = err.error?.detail || err.error?.message || 'No se pudo desactivar al user';
              this.toastService.showError(errMsg);
            }
          });
        }
      );
    }
  }

  // Lógica del Modal
  abrirModalEditar(user: User): void {
    this.editingUser = { ...user };
    this.showModal = true;
  }

  cerrarModal(): void {
    this.showModal = false;
    this.editingUser = {};
  }

  saveUserChanges(): void {
    if (!this.editingUser.id) return;
    
    this.isSaving = true;
    this.userService.updateUser(this.editingUser.id, this.editingUser).subscribe({
      next: () => {
        this.toastService.showSuccess('User actualizado exitosamente');
        this.isSaving = false;
        this.cerrarModal();
        this.loadUsers();
      },
      error: (err: any) => {
        console.error('Error al update user', err);
        const errMsg = err.error?.detail || err.error?.message || 'Error desconocido al update';
        this.toastService.showError(errMsg);
        this.isSaving = false;
      }
    });
  }

  // Lógica de Confirmación
  abrirConfirmacion(mensaje: string, accion: () => void, accionCancelar: () => void = () => {}): void {
    this.mensajeConfirmacion = mensaje;
    this.accionConfirmacion = () => {
      accion();
      this.cerrarConfirmacion();
    };
    this.showConfirmation = true;
    
    // Si queremos ejecutar algo al cancel, lo guardamos o lo ejecutamos directo.
    // Por simplicidad, ejecutaremos accionCancelar() si el user cierra el modal.
    this.cancelarCallback = accionCancelar;
  }

  cancelarCallback: () => void = () => {};

  cerrarConfirmacion(): void {
    this.showConfirmation = false;
    this.mensajeConfirmacion = '';
    this.accionConfirmacion = () => {};
    this.cancelarCallback();
    this.cancelarCallback = () => {};
  }
}
