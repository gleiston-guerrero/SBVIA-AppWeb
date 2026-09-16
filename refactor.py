import os
import re

terms_class = {
    'BitacoraAuditoria': 'AuditLog',
    'ComportamientoVial': 'DrivingBehavior',
    'Escenario': 'Scenario',
    'EstadoSimulacion': 'SimulationState',
    'EstadoUsuario': 'UserState',
    'EvaluacionIa': 'AiEvaluation',
    'EventoVial': 'RoadEvent',
    'HistorialAcceso': 'AccessHistory',
    'Infraccion': 'Infraction',
    'MetricaDesempeno': 'PerformanceMetric',
    'ModeloIa': 'AiModel',
    'NivelDificultad': 'DifficultyLevel',
    'NivelGravedad': 'SeverityLevel',
    'ProgresoSimulacion': 'SimulationProgress',
    'ReglaTransito': 'TrafficRule',
    'Retroalimentacion': 'Feedback',
    'Rol': 'Role',
    'SesionEntrenamiento': 'TrainingSession',
    'Simulacion': 'Simulation',
    'TipoClima': 'WeatherType',
    'TipoEvento': 'EventType',
    'TipoMetrica': 'MetricType',
    'TipoVehiculo': 'VehicleType',
    'TipoVia': 'RoadType',
    'Usuario': 'User',
    'Vehiculo': 'Vehicle'
}

terms_var = {
    'bitacoraAuditoria': 'auditLog',
    'comportamientoVial': 'drivingBehavior',
    'escenario': 'scenario',
    'estadoSimulacion': 'simulationState',
    'estadoUsuario': 'userState',
    'evaluacionIa': 'aiEvaluation',
    'eventoVial': 'roadEvent',
    'historialAcceso': 'accessHistory',
    'infraccion': 'infraction',
    'metricaDesempeno': 'performanceMetric',
    'modeloIa': 'aiModel',
    'nivelDificultad': 'difficultyLevel',
    'nivelGravedad': 'severityLevel',
    'progresoSimulacion': 'simulationProgress',
    'reglaTransito': 'trafficRule',
    'retroalimentacion': 'feedback',
    'rol': 'role',
    'sesionEntrenamiento': 'trainingSession',
    'simulacion': 'simulation',
    'tipoClima': 'weatherType',
    'tipoEvento': 'eventType',
    'tipoMetrica': 'metricType',
    'tipoVehiculo': 'vehicleType',
    'tipoVia': 'roadType',
    'usuario': 'user',
    'vehiculo': 'vehicle',
    
    # Plurals
    'BitacorasAuditoria': 'AuditLogs',
    'ComportamientosViales': 'DrivingBehaviors',
    'Escenarios': 'Scenarios',
    'EstadosSimulacion': 'SimulationStates',
    'EstadosUsuario': 'UserStates',
    'EvaluacionesIa': 'AiEvaluations',
    'EventosViales': 'RoadEvents',
    'HistorialesAcceso': 'AccessHistories',
    'Infracciones': 'Infractions',
    'MetricasDesempeno': 'PerformanceMetrics',
    'ModelosIa': 'AiModels',
    'NivelesDificultad': 'DifficultyLevels',
    'NivelesGravedad': 'SeverityLevels',
    'ProgresosSimulacion': 'SimulationProgresses',
    'ReglasTransito': 'TrafficRules',
    'Retroalimentaciones': 'Feedbacks',
    'Roles': 'Roles',
    'SesionesEntrenamiento': 'TrainingSessions',
    'Simulaciones': 'Simulations',
    'TiposClima': 'WeatherTypes',
    'TiposEvento': 'EventTypes',
    'TiposMetrica': 'MetricTypes',
    'TiposVehiculo': 'VehicleTypes',
    'TiposVia': 'RoadTypes',
    'Usuarios': 'Users',
    'Vehiculos': 'Vehicles',

    'bitacorasAuditoria': 'auditLogs',
    'comportamientosViales': 'drivingBehaviors',
    'escenarios': 'scenarios',
    'estadosSimulacion': 'simulationStates',
    'estadosUsuario': 'userStates',
    'evaluacionesIa': 'aiEvaluations',
    'eventosViales': 'roadEvents',
    'historialesAcceso': 'accessHistories',
    'infracciones': 'infractions',
    'metricasDesempeno': 'performanceMetrics',
    'modelosIa': 'aiModels',
    'nivelesDificultad': 'difficultyLevels',
    'nivelesGravedad': 'severityLevels',
    'progresosSimulacion': 'simulationProgresses',
    'reglasTransito': 'trafficRules',
    'retroalimentaciones': 'feedbacks',
    'roles': 'roles',
    'sesionesEntrenamiento': 'trainingSessions',
    'simulaciones': 'simulations',
    'tiposClima': 'weatherTypes',
    'tiposEvento': 'eventTypes',
    'tiposMetrica': 'metricTypes',
    'tiposVehiculo': 'vehicleTypes',
    'tiposVia': 'roadTypes',
    'usuarios': 'users',
    'vehiculos': 'vehicles',
    
    # Common Fields
    'nombres': 'firstName',
    'apellidos': 'lastName',
    'nombreUsuario': 'username',
    'contrasenaHash': 'passwordHash',
    'telefono': 'phone',
    'fechaNacimiento': 'birthDate',
    'fechaRegistro': 'registrationDate',
    'ultimoAcceso': 'lastAccess',
    'descripcion': 'description',
    'longitudKm': 'lengthKm',
    'tiempoEstimadoMinutos': 'estimatedTimeMinutes',
    'densidadTrafico': 'trafficDensity',
    'fechaCreacion': 'createdAt',
    'idEscenario': 'scenarioId',
    'idUsuario': 'userId',
    'idSimulacion': 'simulationId'
}

all_terms = {**terms_class, **terms_var}

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content
    
    # Do not process SQL migrations
    if filepath.endswith('.sql'):
        return False
        
    # We want to replace terms EXCEPT if they are inside @Table(name="..."), @Column(name="..."), @Procedure(name="...")
    # So we can just find those and preserve them.
    # Actually, the Spanish terms inside `terms_var` are camelCase! 
    # @Table(name="escenario") is lowercase! 
    # `escenario` is in `terms_var`, so it WILL match!
    # Let's temporarily hide `@Table`, `@Column`, `@JoinColumn`, `@Procedure` strings.
    
    hidden_strings = {}
    counter = 0
    
    def hide_match(m):
        nonlocal counter
        key = f"__HIDDEN_{counter}__"
        hidden_strings[key] = m.group(0)
        counter += 1
        return key

    # Regex to hide everything inside @Table(name="..."), @Column(name="..."), @JoinColumn(name="...")
    # This also protects @Procedure(procedureName = "...")
    pattern_hide = r'@(?:Table|Column|JoinColumn|Procedure)\s*\([^)]*name\s*=\s*"[^"]*"[^)]*\)'
    content = re.sub(pattern_hide, hide_match, content)

    # Sort terms by length descending to prevent partial replacements
    for term in sorted(all_terms.keys(), key=len, reverse=True):
        replacement = all_terms[term]
        pattern = r'\b' + re.escape(term) + r'\b'
        content = re.sub(pattern, replacement, content)

    # Restore hidden strings
    for key, val in hidden_strings.items():
        content = content.replace(key, val)

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def process_directory(directory):
    count = 0
    for root, dirs, files in os.walk(directory):
        if any(ex in root for ex in ['node_modules', 'target', 'dist', '.git', 'docs']):
            continue
        for file in files:
            if file.endswith(('.java', '.ts', '.html', '.css', '.xml', '.yml', '.yaml', '.properties')):
                filepath = os.path.join(root, file)
                try:
                    if replace_in_file(filepath):
                        count += 1
                except Exception as e:
                    print(f"Error processing {filepath}: {e}")
    return count

def rename_files(directory):
    for root, dirs, files in os.walk(directory, topdown=False):
        if any(ex in root for ex in ['node_modules', 'target', 'dist', '.git', 'docs']):
            continue
        for name in files:
            new_name = name
            for term in sorted(all_terms.keys(), key=len, reverse=True):
                # only replace if it's not a lowercase string (which could mess up other things)
                # actually it's fine for filenames
                if term in new_name:
                    new_name = new_name.replace(term, all_terms[term])
            
            if new_name != name:
                old_path = os.path.join(root, name)
                new_path = os.path.join(root, new_name)
                os.rename(old_path, new_path)
                print(f"Renamed: {name} -> {new_name}")

        for name in dirs:
            new_name = name
            for term in sorted(all_terms.keys(), key=len, reverse=True):
                if term in new_name:
                    new_name = new_name.replace(term, all_terms[term])
            
            if new_name != name:
                old_path = os.path.join(root, name)
                new_path = os.path.join(root, new_name)
                os.rename(old_path, new_path)
                print(f"Renamed Dir: {name} -> {new_name}")

if __name__ == '__main__':
    backend_dir = 'backend/src/main/java'
    frontend_dir = 'frontend/src'
    
    print("Replacing contents...")
    c1 = process_directory(backend_dir)
    c2 = process_directory(frontend_dir)
    print(f"Modified {c1} backend files and {c2} frontend files.")
    
    print("Renaming files and directories...")
    rename_files(backend_dir)
    rename_files(frontend_dir)
    print("Done.")
