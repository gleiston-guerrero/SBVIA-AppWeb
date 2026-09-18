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
  isLoading = true;
  isSaving = false;
  confirmDeletion?: TrafficRule;
  form: TrafficRule = this.vacio();

  constructor(private reglasService: TrafficRuleService,
              private toast: ToastService) {}

  ngOnInit(): void {
    this.load();
  }

  get filtradas(): TrafficRule[] {
    const texto = this.filtro.trim().toLowerCase();
    return this.reglas.filter(r => !texto || [r.codigo, r.nombre, r.categoria]
      .some(value => value?.toLowerCase().includes(texto)));
  }

  load(): void {
    this.reglasService.list().subscribe({
      next: reglas => { this.reglas = reglas; this.isLoading = false; },
      error: () => { this.toast.showError('No se pudieron load las reglas'); this.isLoading = false; }
    });
  }

  editar(regla: TrafficRule): void { this.form = { ...regla }; window.scrollTo({ top: 0, behavior: 'smooth' }); }
  cancel(): void { this.form = this.vacio(); }

  save(): void {
    if (!this.form.codigo.trim() || !this.form.nombre.trim()
      || !this.form.categoria.trim() || this.form.penalizacionBase < 0) return;
    this.isSaving = true;
    const operation = this.form.id
      ? this.reglasService.update(this.form.id, this.form)
      : this.reglasService.create(this.form);
    operation.subscribe({
      next: () => { this.toast.showSuccess(this.form.id ? 'Regla actualizada correctamente' : 'Regla creada correctamente'); this.cancel(); this.load(); this.isSaving = false; },
      error: (error: any) => { this.toast.showError(error.error?.detail ?? 'No se pudo save la regla'); this.isSaving = false; }
    });
  }

  delete(): void {
    const regla = this.confirmDeletion;
    if (!regla?.id) return;
    this.reglasService.delete(regla.id).subscribe({
      next: () => { this.toast.showSuccess('Regla eliminada correctamente'); this.confirmDeletion = undefined; this.load(); },
      error: (error: any) => this.toast.showError(error.error?.detail ?? 'No se pudo delete la regla')
    });
  }

  private vacio(): TrafficRule {
    return { codigo: '', nombre: '', descripcion: '', categoria: '', penalizacionBase: 0, activa: true };
  }
}
