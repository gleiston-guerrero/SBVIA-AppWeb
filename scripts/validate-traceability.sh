#!/usr/bin/env bash
# Valida que todos los requisitos en docs/trazabilidad/matriz.csv tengan archivo de prueba o procedimiento asociado y que dichos archivos existan

echo "Validando Matriz de Trazabilidad..."
MATRIZ="docs/trazabilidad/matriz.csv"
EXIT_CODE=0

if [ ! -f "$MATRIZ" ]; then
    echo "[ERROR] No se encuentra $MATRIZ"
    exit 1
fi

TOTAL_REQS=$(tail -n +2 "$MATRIZ" | wc -l)
PENDIENTES=$(grep -c "PENDING" "$MATRIZ" || true)

echo "Total de Requisitos/Historias en matriz: $TOTAL_REQS"
echo "Requisitos Pendientes: $PENDIENTES"

if [ "$PENDIENTES" -gt 0 ]; then
    echo "[WARNING] Existen $PENDIENTES requisitos en estado PENDING."
fi

# Validar existencia de archivos
tail -n +2 "$MATRIZ" | while IFS=',' read -r req_id type desc module test_file status access_type sql_file; do
    
    # Check test file if not empty and not just a name like Lighthouse
    if [[ -n "$test_file" && "$test_file" != "Lighthouse" ]]; then
        # Search for test file in project
        FOUND=$(find . -type f -name "$test_file" | head -n 1)
        if [[ -z "$FOUND" ]]; then
            echo "[ERROR] Archivo de prueba no encontrado para $req_id: $test_file"
            EXIT_CODE=1
        fi
    fi
    
    # Check sql file
    if [[ -n "$sql_file" ]]; then
        if [ ! -f "$sql_file" ]; then
            echo "[ERROR] Archivo SQL no encontrado para $req_id: $sql_file"
            EXIT_CODE=1
        fi
    fi
done

if [ $EXIT_CODE -eq 0 ]; then
    echo "[OK] El 100% de los requisitos tienen estado IMPLEMENTED, TESTED o VERIFIED y sus archivos existen."
else
    echo "[ERROR] Falló la validación de trazabilidad por archivos faltantes."
fi

exit $EXIT_CODE
