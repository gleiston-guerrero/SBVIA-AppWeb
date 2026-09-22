import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ScenarioService, Scenario } from './scenario.service';
import { TranslatePipe } from '../../i18n/translate.pipe';
import { LanguageService } from '../../i18n/language.service';

@Component({
  selector: 'app-scenario-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, TranslatePipe],
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
    private scenarioService: ScenarioService,
    private route: ActivatedRoute,
    private router: Router,
    private i18n: LanguageService
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
        this.errorMessage = err.error?.detail ?? this.i18n.t('scenarioForm.loadFailed');
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
          this.errorMessage = this.getErrorMessage(err, this.i18n.t('scenarioForm.updateFailed'));
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
          this.errorMessage = this.getErrorMessage(err, this.i18n.t('scenarioForm.createFailed'));
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
