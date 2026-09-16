import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EscenarioService, Scenario } from './scenario.service';

@Component({
  selector: 'app-scenario-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './scenario-form.component.html',
  styleUrl: './scenario-form.component.css'
})
export class EscenarioFormComponent implements OnInit {
  escenarioForm: FormGroup;
  isEditMode = false;
  escenarioId: number | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private escenarioService: EscenarioService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.escenarioForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      roadType: ['', Validators.required],
      difficultyLevel: ['', Validators.required],
      clima: ['', Validators.required],
      trafficDensity: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.escenarioId = +idParam;
      this.loadEscenarioData(this.escenarioId);
    }
  }

  loadEscenarioData(id: number): void {
    this.loading = true;
    this.escenarioService.buscarPorId(id).subscribe({
      next: (scenario) => {
        this.escenarioForm.patchValue({
          nombre: scenario.nombre,
          description: scenario.description,
          roadType: scenario.roadType,
          difficultyLevel: scenario.difficultyLevel,
          clima: scenario.clima,
          trafficDensity: scenario.trafficDensity
        });
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.detail ?? 'Error al cargar los datos del scenario.';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.escenarioForm.invalid) {
      this.escenarioForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    const escenarioData: Scenario = this.escenarioForm.value;

    if (this.isEditMode && this.escenarioId) {
      this.escenarioService.actualizar(this.escenarioId, escenarioData).subscribe({
        next: () => {
          this.router.navigate(['/scenarios']);
        },
        error: (err) => {
          this.errorMessage = this.obtenerMensajeError(err, 'Error al actualizar el scenario.');
          console.error(err);
          this.loading = false;
        }
      });
    } else {
      this.escenarioService.crear(escenarioData).subscribe({
        next: () => {
          this.router.navigate(['/scenarios']);
        },
        error: (err) => {
          this.errorMessage = this.obtenerMensajeError(err, 'Error al crear el scenario.');
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  private obtenerMensajeError(error: any, mensajePredeterminado: string): string {
    const errores = error.error?.errores;
    if (errores && typeof errores === 'object') {
      return Object.values(errores).join(' ');
    }
    return error.error?.detail ?? mensajePredeterminado;
  }
}
