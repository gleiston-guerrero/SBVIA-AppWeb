import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { RegisterComponent } from './auth/register.component';
import { ShellComponent } from './shared/components/shell/shell.component';
import { authGuard } from './auth/auth.guard';
import { roleGuard } from './auth/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'scenarios', loadComponent: () => import('./features/scenarios/scenario-list.component').then(m => m.ScenarioListComponent) },
      { path: 'scenarios/nuevo', loadComponent: () => import('./features/scenarios/scenario-form.component').then(m => m.ScenarioFormComponent), canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
      { path: 'scenarios/editar/:id', loadComponent: () => import('./features/scenarios/scenario-form.component').then(m => m.ScenarioFormComponent), canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
      { path: 'users', loadComponent: () => import('./features/users/user-list.component').then(m => m.UserListComponent), canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
      { path: 'reglas-transito', loadComponent: () => import('./features/rules/traffic-rule.component').then(m => m.TrafficRuleComponent), canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
      { path: 'practicas', loadComponent: () => import('./features/practices/practices-list.component').then(m => m.PracticesListComponent) },
      { path: 'simulador', loadComponent: () => import('./features/simulator/driving-simulator.component').then(m => m.DrivingSimulatorComponent) },
      { path: 'simulation/:scenarioId', loadComponent: () => import('./features/practices/simulation.component').then(m => m.SimulationComponent) },
      { path: 'instructor', loadComponent: () => import('./features/supervision/supervision.component').then(m => m.SupervisionComponent), canActivate: [roleGuard], data: { roles: ['INSTRUCTOR', 'ADMINISTRADOR'] } },
      { path: 'auditoria', loadComponent: () => import('./features/audit/audit.component').then(m => m.AuditComponent), canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
      { path: 'backups', loadComponent: () => import('./features/backups/backup-management.component').then(m => m.BackupManagementComponent), canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
