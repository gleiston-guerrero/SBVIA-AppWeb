export interface Simulation {
  simulationId: number;
  startDate: string;
  endDate: string | null;
  finalScore: number;
  completed: boolean;
  scenarioId?: number;
  nombreEscenario: string;
  userId?: number;
  username?: string;
  correoUsuario?: string;
}
