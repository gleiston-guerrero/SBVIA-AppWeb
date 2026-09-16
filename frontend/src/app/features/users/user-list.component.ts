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
export class UsuarioListComponent implements OnInit {
  users: User[] = [];
  page = 0;
  size = 10;
  totalPages = 0;
  rolesDisponibles = ['PARTICIPANTE', 'INSTRUCTOR', 'ADMINISTRADOR'];

  get usuariosActivos(): number {
    return this.users.filter(user => !user.cuentaBloqueada).length;
  }

  // Modal de edición
  mostrarModal = false;
  usuarioEditando: Partial<User> = {};
  guardando = false;

  // Modal de Confirmación
  mostrarConfirmacion = false;
  mensajeConfirmacion = '';
  accionConfirmacion: () => void = () => {};

  constructor(
    private usuarioService: UsuarioService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.usuarioService.listar(this.page, this.size).subscribe({
      next: (data) => {
        this.users = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err) => {
        console.error('Error al cargar users', err);
      }
    });
  }

  cambiarPagina(newPage: number): void {
    this.page = newPage;
    this.cargarUsuarios();
  }

  cambiarRol(user: User, nuevoRol: string): void {
    if (user.role === nuevoRol) return;
    
    this.abrirConfirmacion(
      `¿Estás seguro de cambiar el role de ${user.firstName} a ${nuevoRol}?`,
      () => {
        if (user.id !== undefined) {
          this.usuarioService.cambiarRol(user.id, nuevoRol).subscribe({
            next: () => {
              this.toastService.showSuccess('Role actualizado exitosamente');
              this.cargarUsuarios();
            },
            error: (err) => {
              console.error('Error actualizando role', err);
              const errMsg = err.error?.detail || err.error?.message || 'No se pudo actualizar el role';
              this.toastService.showError(errMsg);
              this.cargarUsuarios();
            }
          });
        }
      },
      () => {
        this.cargarUsuarios(); // revert select visual state if cancelled
      }
    );
  }

  desactivarUsuario(id: number | undefined, nombre: string): void {
    if (id !== undefined) {
      this.abrirConfirmacion(
        `¿Estás seguro de desactivar la cuenta de ${nombre}?`,
        () => {
          this.usuarioService.eliminar(id).subscribe({
            next: () => {
              this.toastService.showSuccess('User desactivado exitosamente');
              this.cargarUsuarios();
            },
            error: (err) => {
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
    this.usuarioEditando = { ...user };
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.usuarioEditando = {};
  }

  guardarCambiosUsuario(): void {
    if (!this.usuarioEditando.id) return;
    
    this.guardando = true;
    this.usuarioService.actualizarUsuario(this.usuarioEditando.id, this.usuarioEditando).subscribe({
      next: () => {
        this.toastService.showSuccess('User actualizado exitosamente');
        this.guardando = false;
        this.cerrarModal();
        this.cargarUsuarios();
      },
      error: (err) => {
        console.error('Error al actualizar user', err);
        const errMsg = err.error?.detail || err.error?.message || 'Error desconocido al actualizar';
        this.toastService.showError(errMsg);
        this.guardando = false;
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
    this.mostrarConfirmacion = true;
    
    // Si queremos ejecutar algo al cancelar, lo guardamos o lo ejecutamos directo.
    // Por simplicidad, ejecutaremos accionCancelar() si el user cierra el modal.
    this.cancelarCallback = accionCancelar;
  }

  cancelarCallback: () => void = () => {};

  cerrarConfirmacion(): void {
    this.mostrarConfirmacion = false;
    this.mensajeConfirmacion = '';
    this.accionConfirmacion = () => {};
    this.cancelarCallback();
    this.cancelarCallback = () => {};
  }
}
