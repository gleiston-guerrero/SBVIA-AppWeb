import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BackupService } from './backup.service';
import { BackupRecord } from './backup.model';
import { TranslatePipe } from '../../i18n/translate.pipe';

@Component({
  selector: 'app-backup-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslatePipe],
  templateUrl: './backup-management.component.html',
  styleUrls: ['./backup-management.component.css']
})
export class BackupManagementComponent implements OnInit, OnDestroy {
  backups: BackupRecord[] = [];
  isLoading = true;
  modalAbierto = false;
  generando = false;
  backupToDelete: number | null = null;
  
  // Modal state
  respaldoForm: FormGroup;
  
  private autoRefreshInterval: any;

  constructor(private backupService: BackupService, private fb: FormBuilder) {
    this.respaldoForm = this.fb.group({
      modalidad: ['COMPLETO', Validators.required],
      fechaProgramada: [''],
      comentario: ['']
    });
  }

  ngOnInit(): void {
    this.loadBackups();
    this.autoRefreshInterval = setInterval(() => {
      this.loadBackupsSilently();
    }, 5000);
  }

  ngOnDestroy(): void {
    if (this.autoRefreshInterval) {
      clearInterval(this.autoRefreshInterval);
    }
  }

  loadBackups(): void {
    this.isLoading = true;
    this.backupService.list().subscribe({
      next: (data: any) => {
        this.backups = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  loadBackupsSilently(): void {
    const hayEnProgreso = this.backups.some(r => r.status === 'EN_PROGRESO' || r.status === 'PROGRAMADO');
    if (hayEnProgreso) {
      this.backupService.list().subscribe(data => this.backups = data);
    }
  }

  openModal(): void {
    this.respaldoForm.reset({ modalidad: 'COMPLETO' });
    this.modalAbierto = true;
  }

  closeModal(): void {
    this.modalAbierto = false;
  }

  generateBackup(): void {
    if (this.respaldoForm.invalid) return;
    
    this.generando = true;
    const payload = this.respaldoForm.value;
    
    this.backupService.generate(payload).subscribe({
      next: (nuevoRespaldo) => {
        this.backups.unshift(nuevoRespaldo);
        this.generando = false;
        this.closeModal();
      },
      error: () => {
        this.generando = false;
        this.closeModal();
      }
    });
  }

  download(id: number): void {
    this.backupService.download(id);
  }

  requestDeletion(id: number): void {
    this.backupToDelete = id;
  }

  cancelDeletion(): void {
    this.backupToDelete = null;
  }

  delete(): void {
    if (this.backupToDelete) {
      this.backupService.delete(this.backupToDelete).subscribe(() => {
        this.backups = this.backups.filter(r => r.id !== this.backupToDelete);
        this.backupToDelete = null;
      });
    }
  }

  formatBytes(bytes: number | undefined): string {
    if (!bytes) return '0 B';
    const k = 1024;
    const dm = 2;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
  }
}
