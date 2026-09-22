#!/usr/bin/env python3
"""Genera el mapa espanol->ingles del esquema y la migracion Flyway.

El renombrado es mecanico pero masivo (26 tablas, 211 columnas), asi que se
construye desde un diccionario de tokens mas las excepciones donde el orden de
las palabras en ingles no es el mismo que en espanol.
"""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent

# Tablas ya en ingles: no se tocan.
YA_INGLES = {"audit_log", "backup"}

# Nombres completos de tabla. `usuario` pasa a `users` porque `user` es palabra
# reservada en PostgreSQL y obligaria a entrecomillarla en cada consulta.
TABLAS = {
    "comportamiento_vial": "road_behavior",
    "decision": "decision",
    "escenario": "scenario",
    "estado_simulacion": "simulation_status",
    "estado_usuario": "user_status",
    "evaluacion_ia": "ai_evaluation",
    "evento_vial": "road_event",
    "historial_acceso": "access_history",
    "infraccion": "infraction",
    "metrica_desempeno": "performance_metric",
    "modelo_ia": "ai_model",
    "nivel_dificultad": "difficulty_level",
    "nivel_gravedad": "severity_level",
    "progreso_simulacion": "simulation_progress",
    "regla_transito": "traffic_rule",
    "retroalimentacion": "feedback",
    "rol": "role",
    "sesion_entrenamiento": "training_session",
    "simulacion": "simulation",
    "tipo_clima": "weather_type",
    "tipo_evento": "event_type",
    "tipo_metrica": "metric_type",
    "tipo_vehiculo": "vehicle_type",
    "tipo_via": "road_type",
    "usuario": "users",
    "vehiculo": "vehicle",
}

# Tokens sueltos para las columnas.
TOKENS = {
    "id": "id", "nombre": "name", "nombres": "first_name", "apellidos": "last_name",
    "descripcion": "description", "correo": "email", "nombre_usuario": "username",
    "contrasena_hash": "password_hash", "telefono": "phone", "fecha_nacimiento": "birth_date",
    "fecha_registro": "registered_at", "ultimo_acceso": "last_access_at",
    "intentos_fallidos": "failed_attempts", "cuenta_bloqueada": "account_locked",
    "fecha_creacion": "created_at", "fecha_hora": "occurred_at", "fecha_inicio": "started_at",
    "fecha_fin": "ended_at", "fecha_evaluacion": "evaluated_at",
    "fecha_generacion": "generated_at", "fecha_entrenamiento": "trained_at",
    "fecha_programada": "scheduled_date", "activo": "active", "activa": "active",
    "codigo": "code", "categoria": "category", "penalizacion_base": "base_penalty",
    "penalizacion_aplicada": "applied_penalty", "multiplicador_penalizacion":
    "penalty_multiplier", "valor": "value", "unidad_medida": "unit",
    "valor_minimo": "min_value", "valor_maximo": "max_value",
    "observacion": "note", "observaciones": "notes", "comentario": "comment",
    "recomendacion": "recommendation", "origen": "source",
    "clasificacion": "classification", "nivel_riesgo": "risk_level",
    "puntaje_seguridad": "safety_score", "puntaje_responsabilidad": "responsibility_score",
    "puntaje_cumplimiento": "compliance_score", "puntaje_final": "final_score",
    "accion_realizada": "action_taken", "resultado": "result",
    "tiempo_reaccion_ms": "reaction_time_ms", "posicion_x": "position_x",
    "posicion_y": "position_y", "velocidad_vehiculo_kmh": "vehicle_speed_kmh",
    "velocidad_actual_kmh": "current_speed_kmh", "velocidad_referencial_kmh":
    "reference_speed_kmh", "velocidad_maxima_kmh": "max_speed_kmh",
    "longitud_km": "length_km", "tiempo_estimado_minutos": "estimated_minutes",
    "densidad_trafico": "traffic_density", "porcentaje_progreso": "progress_percentage",
    "porcentaje": "percentage", "etapa": "stage", "duracion_segundos": "duration_seconds",
    "completada": "completed", "numero_intento": "attempt_number",
    "direccion_ip": "ip_address", "dispositivo": "device", "navegador": "browser",
    "acceso_exitoso": "successful_login", "detalle": "detail", "detalles": "details",
    "es_estado_final": "is_final_state", "permite_acceso": "allows_access",
    "nivel_confianza": "confidence_level", "clasificacion_predicha": "predicted_class",
    "datos_entrada": "input_data", "datos_anteriores": "previous_data",
    "datos_nuevos": "new_data", "tipo_modelo": "model_type",
    "precision_modelo": "model_accuracy", "parametros": "parameters",
    "factor_visibilidad": "visibility_factor", "factor_adherencia": "grip_factor",
    "requiere_licencia": "requires_license", "marca": "brand", "modelo": "model",
    "anio": "year", "transmision": "transmission", "potencia_hp": "horsepower",
    "objetivo": "objective", "estado": "status", "modalidad": "mode",
    "tamanio_bytes": "size_bytes", "version": "version", "unidad": "unit",
}

# Nombres de columna cuyo ingles no sale de los tokens.
COLUMNAS_EXACTAS = {
    "nombre_usuario": "username", "es_estado_final": "is_final_state",
    "permite_acceso": "allows_access",
}

# El token de una clave foranea no siempre coincide con el nombre de su tabla.
ALIAS_TABLA = {"comportamiento": "comportamiento_vial", "sesion": "sesion_entrenamiento"}

# En cuatro tablas la clave primaria NO se llama igual que la tabla, asi que la
# regla `id_<tabla>` no la reconoce. Se declaran explicitamente.
PK_DE_TABLA = {
    "comportamiento_vial": "id_comportamiento",
    "metrica_desempeno": "id_metrica",
    "progreso_simulacion": "id_progreso",
    "sesion_entrenamiento": "id_sesion",
}

# Prefijo que recibe la clave foranea, cuando el nombre de la tabla no sirve tal cual.
FK_PREFIJO = {"usuario": "user", "users": "user"}


def ingles_de_tabla(nombre: str) -> str | None:
    if nombre in TABLAS:
        return TABLAS[nombre]
    if nombre in YA_INGLES:
        return nombre
    return None


def traduce_columna(col: str, tabla: str) -> str:
    """Traduce una columna sabiendo a que tabla pertenece.

    La distincion importa: `id_X` es la clave primaria solo dentro de la tabla X.
    En cualquier otra tabla es una clave foranea y debe llamarse `X_id`.
    """
    if col in COLUMNAS_EXACTAS:
        return COLUMNAS_EXACTAS[col]
    if col == PK_DE_TABLA.get(tabla):
        return "id"  # es la clave primaria de esta tabla, aunque no case con su nombre
    if col.startswith("id_"):
        referencia = col[3:]
        if referencia == tabla:
            return "id"  # es la clave primaria de esta misma tabla
        tabla_ref = ALIAS_TABLA.get(referencia, referencia)
        ingles = ingles_de_tabla(tabla_ref)
        if ingles is None:
            return f"{referencia}_id"  # no la reconozco: la dejo legible
        return f"{FK_PREFIJO.get(ingles, ingles)}_id"
    if col in TOKENS:
        return TOKENS[col]
    return "_".join(TOKENS.get(p, p) for p in col.split("_"))


def main() -> int:
    esquema = json.loads((RAIZ / "scripts" / "esquema_actual.json").read_text(encoding="utf-8"))
    mapa = {}
    for tabla, columnas in esquema.items():
        if tabla in YA_INGLES:
            continue
        nueva_tabla = TABLAS.get(tabla)
        if not nueva_tabla:
            print(f"  AVISO: sin nombre ingles para la tabla {tabla}")
            continue
        cols = {}
        for c in columnas:
            nc = traduce_columna(c, tabla)
            # `value` es palabra reservada en H2 v2 y alli no se crea la tabla.
            # En PostgreSQL se admite, pero un nombre tan generico no aporta y
            # rompe las pruebas, asi que se cualifica por tabla.
            if nc == "value":
                nc = {"difficulty_level": "level_value", "severity_level": "severity_value",
                      "performance_metric": "metric_value"}.get(nueva_tabla, nc)
            # YEAR tambien es reservada en H2 v2 y hace fallar la creacion de la tabla.
            if nc == "year":
                nc = "model_year"
            cols[c] = nc
        mapa[tabla] = {"nueva": nueva_tabla, "columnas": cols}

    # Comprobar que no hay nombres repetidos dentro de una tabla
    problemas = []
    for t, d in mapa.items():
        vistos = {}
        for orig, nuevo in d["columnas"].items():
            if nuevo in vistos:
                problemas.append(f"  {t}: {vistos[nuevo]} y {orig} -> {nuevo}")
            vistos[nuevo] = orig
    salida = RAIZ / "scripts" / "mapa_renombrado.json"
    salida.write_text(json.dumps(mapa, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"tablas mapeadas: {len(mapa)}")
    print(f"columnas mapeadas: {sum(len(d['columnas']) for d in mapa.values())}")
    print(f"guardado en {salida.name}")
    if problemas:
        print("\nCOLISIONES (hay que resolverlas antes de generar la migracion):")
        for p in problemas:
            print(p)
    return 0


if __name__ == "__main__":
    sys.exit(main())
