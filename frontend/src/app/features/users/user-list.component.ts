import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UsuarioService, User } from './user.service';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../shared/components/toast/toast.service';
import { TranslatePipe } from '../../i18n/translate.pipe';
import { LanguageService } from '../../i18n/language.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, TranslatePipe],
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
    private toastService: ToastService,
    private i18n: LanguageService
  ) { }

  /** Traduce una clave y sustituye marcadores {nombre}. */
  private tr(clave: string, valores: Record<string, string> = {}): string {
    let texto = this.i18n.t(clave);
    for (const [k, v] of Object.entries(valores)) {
      texto = texto.replace(`{${k}}`, v);
    }
    return texto;
  }

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

  changePage(newPage: number): void {
    this.page = newPage;
    this.loadUsers();
  }

  changeRole(user: User, nuevoRol: string): void {
    if (user.role === nuevoRol) return;
    
    this.openConfirmation(
      this.tr('users.confirmRole', { name: user.firstName, role: nuevoRol }),
      () => {
        if (user.id !== undefined) {
          this.userService.changeRole(user.id, nuevoRol).subscribe({
            next: () => {
              this.toastService.showSuccess(this.i18n.t('users.roleUpdated'));
              this.loadUsers();
            },
            error: (err: any) => {
              console.error('Error actualizando role', err);
              const errMsg = err.error?.detail || err.error?.message || this.i18n.t('users.roleUpdateFailed');
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

  deactivateUser(id: number | undefined, nombre: string): void {
    if (id !== undefined) {
      this.openConfirmation(
        this.tr('users.confirmDeactivate', { name: nombre }),
        () => {
          this.userService.delete(id).subscribe({
            next: () => {
              this.toastService.showSuccess(this.i18n.t('users.deactivated'));
              this.loadUsers();
            },
            error: (err: any) => {
              console.error('Error al desactivar user', err);
              const errMsg = err.error?.detail || err.error?.message || this.i18n.t('users.deactivateFailed');
              this.toastService.showError(errMsg);
            }
          });
        }
      );
    }
  }

  // Lógica del Modal
  openEditModal(user: User): void {
    this.editingUser = { ...user };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.editingUser = {};
  }

  saveUserChanges(): void {
    if (!this.editingUser.id) return;
    
    this.isSaving = true;
    this.userService.updateUser(this.editingUser.id, this.editingUser).subscribe({
      next: () => {
        this.toastService.showSuccess(this.i18n.t('users.updated'));
        this.isSaving = false;
        this.closeModal();
        this.loadUsers();
      },
      error: (err: any) => {
        console.error('Error al update user', err);
        const errMsg = err.error?.detail || err.error?.message || this.i18n.t('users.updateFailed');
        this.toastService.showError(errMsg);
        this.isSaving = false;
      }
    });
  }

  // Lógica de Confirmación
  openConfirmation(mensaje: string, accion: () => void, accionCancelar: () => void = () => {}): void {
    this.mensajeConfirmacion = mensaje;
    this.accionConfirmacion = () => {
      accion();
      this.closeConfirmation();
    };
    this.showConfirmation = true;
    
    // Si queremos ejecutar algo al cancel, lo guardamos o lo ejecutamos directo.
    // Por simplicidad, ejecutaremos accionCancelar() si el user cierra el modal.
    this.cancelarCallback = accionCancelar;
  }

  cancelarCallback: () => void = () => {};

  closeConfirmation(): void {
    this.showConfirmation = false;
    this.mensajeConfirmacion = '';
    this.accionConfirmacion = () => {};
    this.cancelarCallback();
    this.cancelarCallback = () => {};
  }
}
