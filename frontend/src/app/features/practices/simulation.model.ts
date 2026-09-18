export interface Simulation {
  simulationId: number;
  startDate: string;
  endDate: string | null;
  finalScore: number;
  completed: boolean;
  scenarioId?: number;
  scenarioName: string;
  userId?: number;
  username?: string;
  userEmail?: string;
}
