export interface Simulation {
  simulationId: number;
  fechaInicio: string;
  fechaFin: string | null;
  puntajeFinal: number;
  completada: boolean;
  scenarioId?: number;
  nombreEscenario: string;
  userId?: number;
  username?: string;
  correoUsuario?: string;
}
