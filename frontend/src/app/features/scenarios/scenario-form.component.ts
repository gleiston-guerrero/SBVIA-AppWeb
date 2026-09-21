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
export class ScenarioFormComponent implements OnInit {
  escenarioForm: FormGroup;
  isEditMode = false;
  escenarioId: number | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private scenarioService: EscenarioService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.escenarioForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      roadType: ['', Validators.required],
      difficultyLevel: ['', Validators.required],
      weatherType: ['', Validators.required],
      trafficDensity: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.escenarioId = +idParam;
      this.loadScenarioData(this.escenarioId);
    }
  }

  loadScenarioData(id: number): void {
    this.loading = true;
    this.scenarioService.findById(id).subscribe({
      next: (scenario) => {
        this.escenarioForm.patchValue({
          name: scenario.name,
          description: scenario.description,
          roadType: scenario.roadType,
          difficultyLevel: scenario.difficultyLevel,
          weatherType: scenario.weatherType,
          trafficDensity: scenario.trafficDensity
        });
        this.loading = false;
      },
      error: (err: any) => {
        this.errorMessage = err.error?.detail ?? 'Error al load los datos del scenario.';
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
      this.scenarioService.update(this.escenarioId, escenarioData).subscribe({
        next: () => {
          this.router.navigate(['/scenarios']);
        },
        error: (err: any) => {
          this.errorMessage = this.getErrorMessage(err, 'Error al update el scenario.');
          console.error(err);
          this.loading = false;
        }
      });
    } else {
      this.scenarioService.create(escenarioData).subscribe({
        next: () => {
          this.router.navigate(['/scenarios']);
        },
        error: (err: any) => {
          this.errorMessage = this.getErrorMessage(err, 'Error al create el scenario.');
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  private getErrorMessage(error: any, defaultMessage: string): string {
    const errores = error.error?.errores;
    if (errores && typeof errores === 'object') {
      return Object.values(errores).join(' ');
    }
    return error.error?.detail ?? defaultMessage;
  }
}
