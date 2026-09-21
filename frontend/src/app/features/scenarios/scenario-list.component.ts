import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EscenarioService, Scenario } from './scenario.service';
import { AuthService } from '../../auth/auth.service';
import { RouterLink } from '@angular/router';
import { ToastService } from '../../shared/components/toast/toast.service';
import { TranslatePipe } from '../../i18n/translate.pipe';

@Component({
  selector: 'app-scenario-list',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslatePipe],
  templateUrl: './scenario-list.component.html',
  styleUrl: './scenario-list.component.css'
})
export class ScenarioListComponent implements OnInit {
  scenarios: Scenario[] = [];
  page = 0;
  totalPages = 0;
  isAdmin = false;
  scenarioToDelete?: Scenario;
  isDeleting = false;

  constructor(
    private scenarioService: EscenarioService,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    this.isAdmin = user?.role === 'ADMINISTRADOR';
    this.loadScenarios();
  }

  loadScenarios(): void {
    this.scenarioService.list(this.page).subscribe({
      next: (data: any) => {
        this.scenarios = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err: any) => console.error('Error isLoading scenarios', err)
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPages) {
      this.page = nuevaPagina;
      this.loadScenarios();
    }
  }

  solicitarEliminacion(scenario: Scenario): void {
    this.scenarioToDelete = scenario;
  }

  cancelarEliminacion(): void {
    if (!this.isDeleting) this.scenarioToDelete = undefined;
  }

  confirmDeletion(): void {
    const id = this.scenarioToDelete?.id;
    if (id === undefined || this.isDeleting) return;
    this.isDeleting = true;
    this.scenarioService.delete(id).subscribe({
      next: () => {
        this.toastService.showSuccess('Scenario eliminado correctamente');
        this.scenarioToDelete = undefined;
        this.isDeleting = false;
        this.loadScenarios();
      },
      error: (err: any) => {
        console.error('Error al delete scenario', err);
        this.toastService.showError(err.error?.detail ?? 'No se pudo delete el scenario');
        this.isDeleting = false;
      }
    });
  }
}
