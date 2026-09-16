import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../shared/components/toast/toast.service';
import { TrafficRule, TrafficRuleService } from './traffic-rule.service';

@Component({
  selector: 'app-traffic-rule', standalone: true, imports: [CommonModule, FormsModule],
  templateUrl: './traffic-rule.component.html', styleUrl: './traffic-rule.component.css'
})
export class TrafficRuleComponent implements OnInit {
  reglas: TrafficRule[] = [];
  filtro = '';
  cargando = true;
  guardando = false;
  confirmarEliminacion?: TrafficRule;
  formulario: TrafficRule = this.vacio();

  constructor(private reglasService: TrafficRuleService,
              private toast: ToastService) {}

  ngOnInit(): void {
    this.cargar();
  }

  get filtradas(): TrafficRule[] {
    const texto = this.filtro.trim().toLowerCase();
    return this.reglas.filter(r => !texto || [r.codigo, r.nombre, r.categoria]
      .some(value => value?.toLowerCase().includes(texto)));
  }

  cargar(): void {
    this.reglasService.listar().subscribe({
      next: reglas => { this.reglas = reglas; this.cargando = false; },
      error: () => { this.toast.showError('No se pudieron cargar las reglas'); this.cargando = false; }
    });
  }

  editar(regla: TrafficRule): void { this.formulario = { ...regla }; window.scrollTo({ top: 0, behavior: 'smooth' }); }
  cancelar(): void { this.formulario = this.vacio(); }

  guardar(): void {
    if (!this.formulario.codigo.trim() || !this.formulario.nombre.trim()
      || !this.formulario.categoria.trim() || this.formulario.penalizacionBase < 0) return;
    this.guardando = true;
    const operation = this.formulario.id
      ? this.reglasService.actualizar(this.formulario.id, this.formulario)
      : this.reglasService.crear(this.formulario);
    operation.subscribe({
      next: () => { this.toast.showSuccess(this.formulario.id ? 'Regla actualizada correctamente' : 'Regla creada correctamente'); this.cancelar(); this.cargar(); this.guardando = false; },
      error: (error: any) => { this.toast.showError(error.error?.detail ?? 'No se pudo guardar la regla'); this.guardando = false; }
    });
  }

  eliminar(): void {
    const regla = this.confirmarEliminacion;
    if (!regla?.id) return;
    this.reglasService.eliminar(regla.id).subscribe({
      next: () => { this.toast.showSuccess('Regla eliminada correctamente'); this.confirmarEliminacion = undefined; this.cargar(); },
      error: (error: any) => this.toast.showError(error.error?.detail ?? 'No se pudo eliminar la regla')
    });
  }

  private vacio(): TrafficRule {
    return { codigo: '', nombre: '', description: '', categoria: '', penalizacionBase: 0, activa: true };
  }
}
