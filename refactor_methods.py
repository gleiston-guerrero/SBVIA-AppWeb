import os
import re

terms_var = {
    # Method prefixes
    'getNombres': 'getFirstName',
    'setNombres': 'setFirstName',
    'getApellidos': 'getLastName',
    'setApellidos': 'setLastName',
    'getNombreUsuario': 'getUsername',
    'setNombreUsuario': 'setUsername',
    'getContrasenaHash': 'getPasswordHash',
    'setContrasenaHash': 'setPasswordHash',
    'getTelefono': 'getPhone',
    'setTelefono': 'setPhone',
    'getFechaNacimiento': 'getBirthDate',
    'setFechaNacimiento': 'setBirthDate',
    'getFechaRegistro': 'getRegistrationDate',
    'setFechaRegistro': 'setRegistrationDate',
    'getUltimoAcceso': 'getLastAccess',
    'setUltimoAcceso': 'setLastAccess',
    'getDescripcion': 'getDescription',
    'setDescripcion': 'setDescription',
    'getLongitudKm': 'getLengthKm',
    'setLongitudKm': 'setLengthKm',
    'getTiempoEstimadoMinutos': 'getEstimatedTimeMinutes',
    'setTiempoEstimadoMinutos': 'setEstimatedTimeMinutes',
    'getDensidadTrafico': 'getTrafficDensity',
    'setDensidadTrafico': 'setTrafficDensity',
    'getFechaCreacion': 'getCreatedAt',
    'setFechaCreacion': 'setCreatedAt',
    
    # Class based get/set
    'getRol': 'getRole',
    'setRol': 'setRole',
    'getRoles': 'getRoles',
    'setRoles': 'setRoles',
    'getEscenario': 'getScenario',
    'setEscenario': 'setScenario',
    'getEscenarios': 'getScenarios',
    'setEscenarios': 'setScenarios',
    'getUsuario': 'getUser',
    'setUsuario': 'setUser',
    'getUsuarios': 'getUsers',
    'setUsuarios': 'setUsers',
    'getSimulacion': 'getSimulation',
    'setSimulacion': 'setSimulation',
    'getSimulaciones': 'getSimulations',
    'setSimulaciones': 'setSimulations',
    
    'getIdEscenario': 'getScenarioId',
    'setIdEscenario': 'setScenarioId',
    'getIdUsuario': 'getUserId',
    'setIdUsuario': 'setUserId',
    'getIdSimulacion': 'getSimulationId',
    'setIdSimulacion': 'setSimulationId',
    
    'getCorreo': 'getEmail',
    'setCorreo': 'setEmail',
    'correo': 'email'
}

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content
    if filepath.endswith('.sql'):
        return False
        
    hidden_strings = {}
    counter = 0
    def hide_match(m):
        nonlocal counter
        key = f"__HIDDEN_{counter}__"
        hidden_strings[key] = m.group(0)
        counter += 1
        return key

    pattern_hide = r'@(?:Table|Column|JoinColumn|Procedure)\s*\([^)]*name\s*=\s*"[^"]*"[^)]*\)'
    content = re.sub(pattern_hide, hide_match, content)

    for term in sorted(terms_var.keys(), key=len, reverse=True):
        replacement = terms_var[term]
        pattern = r'\b' + re.escape(term) + r'\b'
        content = re.sub(pattern, replacement, content)

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
                    pass
    return count

if __name__ == '__main__':
    backend_dir = 'backend/src/main/java'
    frontend_dir = 'frontend/src'
    print("Replacing contents...")
    c1 = process_directory(backend_dir)
    c2 = process_directory(frontend_dir)
    print(f"Modified {c1} backend files and {c2} frontend files.")
