import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EscenarioService, Scenario } from './scenario.service';
import { AuthService } from '../../auth/auth.service';
import { RouterLink } from '@angular/router';
import { ToastService } from '../../shared/components/toast/toast.service';

@Component({
  selector: 'app-scenario-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './scenario-list.component.html',
  styleUrl: './scenario-list.component.css'
})
export class ScenarioListComponent implements OnInit {
  scenarios: Scenario[] = [];
  page = 0;
  totalPages = 0;
  isAdmin = false;
  escenarioAEliminar?: Scenario;
  eliminando = false;

  constructor(
    private escenarioService: EscenarioService,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    this.isAdmin = user?.role === 'ADMINISTRADOR';
    this.cargarEscenarios();
  }

  cargarEscenarios(): void {
    this.escenarioService.listar(this.page).subscribe({
      next: (data: any) => {
        this.scenarios = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err: any) => console.error('Error cargando scenarios', err)
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPages) {
      this.page = nuevaPagina;
      this.cargarEscenarios();
    }
  }

  solicitarEliminacion(scenario: Scenario): void {
    this.escenarioAEliminar = scenario;
  }

  cancelarEliminacion(): void {
    if (!this.eliminando) this.escenarioAEliminar = undefined;
  }

  confirmarEliminacion(): void {
    const id = this.escenarioAEliminar?.id;
    if (id === undefined || this.eliminando) return;
    this.eliminando = true;
    this.escenarioService.eliminar(id).subscribe({
      next: () => {
        this.toastService.showSuccess('Scenario eliminado correctamente');
        this.escenarioAEliminar = undefined;
        this.eliminando = false;
        this.cargarEscenarios();
      },
      error: (err: any) => {
        console.error('Error al eliminar scenario', err);
        this.toastService.showError(err.error?.detail ?? 'No se pudo eliminar el scenario');
        this.eliminando = false;
      }
    });
  }
}
