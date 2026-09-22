-- Renombra al ingles las 26 tablas que quedaban en espanol, sus columnas
-- y los objetos dependientes. Generado desde el esquema real por
-- scripts/generar_v19.py; no editar a mano.

SET client_min_messages TO WARNING;

-- comportamiento_vial -> road_behavior
ALTER TABLE "comportamiento_vial" RENAME TO "road_behavior";
ALTER TABLE "road_behavior" RENAME COLUMN "id_comportamiento" TO "id";
ALTER TABLE "road_behavior" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "road_behavior" RENAME COLUMN "clasificacion" TO "classification";
ALTER TABLE "road_behavior" RENAME COLUMN "nivel_riesgo" TO "risk_level";
ALTER TABLE "road_behavior" RENAME COLUMN "puntaje_seguridad" TO "safety_score";
ALTER TABLE "road_behavior" RENAME COLUMN "puntaje_responsabilidad" TO "responsibility_score";
ALTER TABLE "road_behavior" RENAME COLUMN "puntaje_cumplimiento" TO "compliance_score";
ALTER TABLE "road_behavior" RENAME COLUMN "observaciones" TO "notes";
ALTER TABLE "road_behavior" RENAME COLUMN "fecha_evaluacion" TO "evaluated_at";

-- decision (solo columnas)
ALTER TABLE "decision" RENAME COLUMN "id_decision" TO "id";
ALTER TABLE "decision" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "decision" RENAME COLUMN "id_evento_vial" TO "road_event_id";
ALTER TABLE "decision" RENAME COLUMN "accion_realizada" TO "action_taken";
ALTER TABLE "decision" RENAME COLUMN "resultado" TO "result";
ALTER TABLE "decision" RENAME COLUMN "tiempo_reaccion_ms" TO "reaction_time_ms";
ALTER TABLE "decision" RENAME COLUMN "fecha_hora" TO "occurred_at";
ALTER TABLE "decision" RENAME COLUMN "posicion_x" TO "position_x";
ALTER TABLE "decision" RENAME COLUMN "posicion_y" TO "position_y";
ALTER TABLE "decision" RENAME COLUMN "observacion" TO "note";

-- escenario -> scenario
ALTER TABLE "escenario" RENAME TO "scenario";
ALTER TABLE "scenario" RENAME COLUMN "id_escenario" TO "id";
ALTER TABLE "scenario" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "scenario" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "scenario" RENAME COLUMN "longitud_km" TO "length_km";
ALTER TABLE "scenario" RENAME COLUMN "tiempo_estimado_minutos" TO "estimated_minutes";
ALTER TABLE "scenario" RENAME COLUMN "densidad_trafico" TO "traffic_density";
ALTER TABLE "scenario" RENAME COLUMN "fecha_creacion" TO "created_at";
ALTER TABLE "scenario" RENAME COLUMN "activo" TO "active";
ALTER TABLE "scenario" RENAME COLUMN "id_tipo_via" TO "road_type_id";
ALTER TABLE "scenario" RENAME COLUMN "id_nivel_dificultad" TO "difficulty_level_id";
ALTER TABLE "scenario" RENAME COLUMN "id_tipo_clima" TO "weather_type_id";

-- estado_simulacion -> simulation_status
ALTER TABLE "estado_simulacion" RENAME TO "simulation_status";
ALTER TABLE "simulation_status" RENAME COLUMN "id_estado_simulacion" TO "id";
ALTER TABLE "simulation_status" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "simulation_status" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "simulation_status" RENAME COLUMN "es_estado_final" TO "is_final_state";

-- estado_usuario -> user_status
ALTER TABLE "estado_usuario" RENAME TO "user_status";
ALTER TABLE "user_status" RENAME COLUMN "id_estado_usuario" TO "id";
ALTER TABLE "user_status" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "user_status" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "user_status" RENAME COLUMN "permite_acceso" TO "allows_access";

-- evaluacion_ia -> ai_evaluation
ALTER TABLE "evaluacion_ia" RENAME TO "ai_evaluation";
ALTER TABLE "ai_evaluation" RENAME COLUMN "id_evaluacion_ia" TO "id";
ALTER TABLE "ai_evaluation" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "ai_evaluation" RENAME COLUMN "id_modelo_ia" TO "ai_model_id";
ALTER TABLE "ai_evaluation" RENAME COLUMN "resultado" TO "result";
ALTER TABLE "ai_evaluation" RENAME COLUMN "clasificacion_predicha" TO "predicted_class";
ALTER TABLE "ai_evaluation" RENAME COLUMN "nivel_confianza" TO "confidence_level";
ALTER TABLE "ai_evaluation" RENAME COLUMN "recomendacion" TO "recommendation";
ALTER TABLE "ai_evaluation" RENAME COLUMN "datos_entrada" TO "input_data";
ALTER TABLE "ai_evaluation" RENAME COLUMN "fecha_evaluacion" TO "evaluated_at";

-- evento_vial -> road_event
ALTER TABLE "evento_vial" RENAME TO "road_event";
ALTER TABLE "road_event" RENAME COLUMN "id_evento_vial" TO "id";
ALTER TABLE "road_event" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "road_event" RENAME COLUMN "id_tipo_evento" TO "event_type_id";
ALTER TABLE "road_event" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "road_event" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "road_event" RENAME COLUMN "nivel_riesgo" TO "risk_level";
ALTER TABLE "road_event" RENAME COLUMN "fecha_hora" TO "occurred_at";
ALTER TABLE "road_event" RENAME COLUMN "posicion_x" TO "position_x";
ALTER TABLE "road_event" RENAME COLUMN "posicion_y" TO "position_y";
ALTER TABLE "road_event" RENAME COLUMN "velocidad_vehiculo_kmh" TO "vehicle_speed_kmh";

-- historial_acceso -> access_history
ALTER TABLE "historial_acceso" RENAME TO "access_history";
ALTER TABLE "access_history" RENAME COLUMN "id_historial_acceso" TO "id";
ALTER TABLE "access_history" RENAME COLUMN "id_usuario" TO "user_id";
ALTER TABLE "access_history" RENAME COLUMN "fecha_hora" TO "occurred_at";
ALTER TABLE "access_history" RENAME COLUMN "direccion_ip" TO "ip_address";
ALTER TABLE "access_history" RENAME COLUMN "dispositivo" TO "device";
ALTER TABLE "access_history" RENAME COLUMN "navegador" TO "browser";
ALTER TABLE "access_history" RENAME COLUMN "acceso_exitoso" TO "successful_login";
ALTER TABLE "access_history" RENAME COLUMN "detalle" TO "detail";

-- infraccion -> infraction
ALTER TABLE "infraccion" RENAME TO "infraction";
ALTER TABLE "infraction" RENAME COLUMN "id_infraccion" TO "id";
ALTER TABLE "infraction" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "infraction" RENAME COLUMN "id_decision" TO "decision_id";
ALTER TABLE "infraction" RENAME COLUMN "id_regla_transito" TO "traffic_rule_id";
ALTER TABLE "infraction" RENAME COLUMN "id_nivel_gravedad" TO "severity_level_id";
ALTER TABLE "infraction" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "infraction" RENAME COLUMN "penalizacion_aplicada" TO "applied_penalty";
ALTER TABLE "infraction" RENAME COLUMN "fecha_hora" TO "occurred_at";

-- metrica_desempeno -> performance_metric
ALTER TABLE "metrica_desempeno" RENAME TO "performance_metric";
ALTER TABLE "performance_metric" RENAME COLUMN "id_metrica" TO "id";
ALTER TABLE "performance_metric" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "performance_metric" RENAME COLUMN "id_tipo_metrica" TO "metric_type_id";
ALTER TABLE "performance_metric" RENAME COLUMN "valor" TO "value";
ALTER TABLE "performance_metric" RENAME COLUMN "fecha_hora" TO "occurred_at";
ALTER TABLE "performance_metric" RENAME COLUMN "observacion" TO "note";

-- modelo_ia -> ai_model
ALTER TABLE "modelo_ia" RENAME TO "ai_model";
ALTER TABLE "ai_model" RENAME COLUMN "id_modelo_ia" TO "id";
ALTER TABLE "ai_model" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "ai_model" RENAME COLUMN "tipo_modelo" TO "model_type";
ALTER TABLE "ai_model" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "ai_model" RENAME COLUMN "fecha_entrenamiento" TO "trained_at";
ALTER TABLE "ai_model" RENAME COLUMN "precision_modelo" TO "model_accuracy";
ALTER TABLE "ai_model" RENAME COLUMN "parametros" TO "parameters";
ALTER TABLE "ai_model" RENAME COLUMN "activo" TO "active";

-- nivel_dificultad -> difficulty_level
ALTER TABLE "nivel_dificultad" RENAME TO "difficulty_level";
ALTER TABLE "difficulty_level" RENAME COLUMN "id_nivel_dificultad" TO "id";
ALTER TABLE "difficulty_level" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "difficulty_level" RENAME COLUMN "valor" TO "value";
ALTER TABLE "difficulty_level" RENAME COLUMN "descripcion" TO "description";

-- nivel_gravedad -> severity_level
ALTER TABLE "nivel_gravedad" RENAME TO "severity_level";
ALTER TABLE "severity_level" RENAME COLUMN "id_nivel_gravedad" TO "id";
ALTER TABLE "severity_level" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "severity_level" RENAME COLUMN "valor" TO "value";
ALTER TABLE "severity_level" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "severity_level" RENAME COLUMN "multiplicador_penalizacion" TO "penalty_multiplier";

-- progreso_simulacion -> simulation_progress
ALTER TABLE "progreso_simulacion" RENAME TO "simulation_progress";
ALTER TABLE "simulation_progress" RENAME COLUMN "id_progreso" TO "id";
ALTER TABLE "simulation_progress" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "simulation_progress" RENAME COLUMN "porcentaje" TO "percentage";
ALTER TABLE "simulation_progress" RENAME COLUMN "etapa" TO "stage";
ALTER TABLE "simulation_progress" RENAME COLUMN "posicion_x" TO "position_x";
ALTER TABLE "simulation_progress" RENAME COLUMN "posicion_y" TO "position_y";
ALTER TABLE "simulation_progress" RENAME COLUMN "velocidad_actual_kmh" TO "current_speed_kmh";
ALTER TABLE "simulation_progress" RENAME COLUMN "fecha_hora" TO "occurred_at";

-- regla_transito -> traffic_rule
ALTER TABLE "regla_transito" RENAME TO "traffic_rule";
ALTER TABLE "traffic_rule" RENAME COLUMN "id_regla_transito" TO "id";
ALTER TABLE "traffic_rule" RENAME COLUMN "codigo" TO "code";
ALTER TABLE "traffic_rule" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "traffic_rule" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "traffic_rule" RENAME COLUMN "categoria" TO "category";
ALTER TABLE "traffic_rule" RENAME COLUMN "penalizacion_base" TO "base_penalty";
ALTER TABLE "traffic_rule" RENAME COLUMN "activa" TO "active";

-- retroalimentacion -> feedback
ALTER TABLE "retroalimentacion" RENAME TO "feedback";
ALTER TABLE "feedback" RENAME COLUMN "id_retroalimentacion" TO "id";
ALTER TABLE "feedback" RENAME COLUMN "id_simulacion" TO "simulation_id";
ALTER TABLE "feedback" RENAME COLUMN "id_comportamiento" TO "road_behavior_id";
ALTER TABLE "feedback" RENAME COLUMN "comentario" TO "comment";
ALTER TABLE "feedback" RENAME COLUMN "recomendacion" TO "recommendation";
ALTER TABLE "feedback" RENAME COLUMN "origen" TO "source";
ALTER TABLE "feedback" RENAME COLUMN "fecha_generacion" TO "generated_at";

-- rol -> role
ALTER TABLE "rol" RENAME TO "role";
ALTER TABLE "role" RENAME COLUMN "id_rol" TO "id";
ALTER TABLE "role" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "role" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "role" RENAME COLUMN "fecha_creacion" TO "created_at";
ALTER TABLE "role" RENAME COLUMN "activo" TO "active";

-- sesion_entrenamiento -> training_session
ALTER TABLE "sesion_entrenamiento" RENAME TO "training_session";
ALTER TABLE "training_session" RENAME COLUMN "id_sesion" TO "id";
ALTER TABLE "training_session" RENAME COLUMN "id_usuario" TO "user_id";
ALTER TABLE "training_session" RENAME COLUMN "fecha_inicio" TO "started_at";
ALTER TABLE "training_session" RENAME COLUMN "fecha_fin" TO "ended_at";
ALTER TABLE "training_session" RENAME COLUMN "estado" TO "status";
ALTER TABLE "training_session" RENAME COLUMN "objetivo" TO "objective";
ALTER TABLE "training_session" RENAME COLUMN "observaciones" TO "notes";

-- simulacion -> simulation
ALTER TABLE "simulacion" RENAME TO "simulation";
ALTER TABLE "simulation" RENAME COLUMN "id_simulacion" TO "id";
ALTER TABLE "simulation" RENAME COLUMN "id_sesion" TO "training_session_id";
ALTER TABLE "simulation" RENAME COLUMN "id_usuario" TO "user_id";
ALTER TABLE "simulation" RENAME COLUMN "id_escenario" TO "scenario_id";
ALTER TABLE "simulation" RENAME COLUMN "id_vehiculo" TO "vehicle_id";
ALTER TABLE "simulation" RENAME COLUMN "id_estado_simulacion" TO "simulation_status_id";
ALTER TABLE "simulation" RENAME COLUMN "numero_intento" TO "attempt_number";
ALTER TABLE "simulation" RENAME COLUMN "fecha_inicio" TO "started_at";
ALTER TABLE "simulation" RENAME COLUMN "fecha_fin" TO "ended_at";
ALTER TABLE "simulation" RENAME COLUMN "porcentaje_progreso" TO "progress_percentage";
ALTER TABLE "simulation" RENAME COLUMN "puntaje_final" TO "final_score";
ALTER TABLE "simulation" RENAME COLUMN "duracion_segundos" TO "duration_seconds";
ALTER TABLE "simulation" RENAME COLUMN "completada" TO "completed";
ALTER TABLE "simulation" RENAME COLUMN "observaciones" TO "notes";

-- tipo_clima -> weather_type
ALTER TABLE "tipo_clima" RENAME TO "weather_type";
ALTER TABLE "weather_type" RENAME COLUMN "id_tipo_clima" TO "id";
ALTER TABLE "weather_type" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "weather_type" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "weather_type" RENAME COLUMN "factor_visibilidad" TO "visibility_factor";
ALTER TABLE "weather_type" RENAME COLUMN "factor_adherencia" TO "grip_factor";

-- tipo_evento -> event_type
ALTER TABLE "tipo_evento" RENAME TO "event_type";
ALTER TABLE "event_type" RENAME COLUMN "id_tipo_evento" TO "id";
ALTER TABLE "event_type" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "event_type" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "event_type" RENAME COLUMN "categoria" TO "category";

-- tipo_metrica -> metric_type
ALTER TABLE "tipo_metrica" RENAME TO "metric_type";
ALTER TABLE "metric_type" RENAME COLUMN "id_tipo_metrica" TO "id";
ALTER TABLE "metric_type" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "metric_type" RENAME COLUMN "unidad_medida" TO "unit";
ALTER TABLE "metric_type" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "metric_type" RENAME COLUMN "valor_minimo" TO "min_value";
ALTER TABLE "metric_type" RENAME COLUMN "valor_maximo" TO "max_value";

-- tipo_vehiculo -> vehicle_type
ALTER TABLE "tipo_vehiculo" RENAME TO "vehicle_type";
ALTER TABLE "vehicle_type" RENAME COLUMN "id_tipo_vehiculo" TO "id";
ALTER TABLE "vehicle_type" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "vehicle_type" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "vehicle_type" RENAME COLUMN "requiere_licencia" TO "requires_license";

-- tipo_via -> road_type
ALTER TABLE "tipo_via" RENAME TO "road_type";
ALTER TABLE "road_type" RENAME COLUMN "id_tipo_via" TO "id";
ALTER TABLE "road_type" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "road_type" RENAME COLUMN "descripcion" TO "description";
ALTER TABLE "road_type" RENAME COLUMN "velocidad_referencial_kmh" TO "reference_speed_kmh";

-- usuario -> users
ALTER TABLE "usuario" RENAME TO "users";
ALTER TABLE "users" RENAME COLUMN "id_usuario" TO "id";
ALTER TABLE "users" RENAME COLUMN "nombres" TO "first_name";
ALTER TABLE "users" RENAME COLUMN "apellidos" TO "last_name";
ALTER TABLE "users" RENAME COLUMN "correo" TO "email";
ALTER TABLE "users" RENAME COLUMN "nombre_usuario" TO "username";
ALTER TABLE "users" RENAME COLUMN "contrasena_hash" TO "password_hash";
ALTER TABLE "users" RENAME COLUMN "telefono" TO "phone";
ALTER TABLE "users" RENAME COLUMN "fecha_nacimiento" TO "birth_date";
ALTER TABLE "users" RENAME COLUMN "fecha_registro" TO "registered_at";
ALTER TABLE "users" RENAME COLUMN "ultimo_acceso" TO "last_access_at";
ALTER TABLE "users" RENAME COLUMN "intentos_fallidos" TO "failed_attempts";
ALTER TABLE "users" RENAME COLUMN "cuenta_bloqueada" TO "account_locked";
ALTER TABLE "users" RENAME COLUMN "id_rol" TO "role_id";
ALTER TABLE "users" RENAME COLUMN "id_estado_usuario" TO "user_status_id";

-- vehiculo -> vehicle
ALTER TABLE "vehiculo" RENAME TO "vehicle";
ALTER TABLE "vehicle" RENAME COLUMN "id_vehiculo" TO "id";
ALTER TABLE "vehicle" RENAME COLUMN "nombre" TO "name";
ALTER TABLE "vehicle" RENAME COLUMN "marca" TO "brand";
ALTER TABLE "vehicle" RENAME COLUMN "modelo" TO "model";
ALTER TABLE "vehicle" RENAME COLUMN "anio" TO "year";
ALTER TABLE "vehicle" RENAME COLUMN "transmision" TO "transmission";
ALTER TABLE "vehicle" RENAME COLUMN "velocidad_maxima_kmh" TO "max_speed_kmh";
ALTER TABLE "vehicle" RENAME COLUMN "potencia_hp" TO "horsepower";
ALTER TABLE "vehicle" RENAME COLUMN "activo" TO "active";
ALTER TABLE "vehicle" RENAME COLUMN "id_tipo_vehiculo" TO "vehicle_type_id";

-- Secuencias de las claves primarias
ALTER SEQUENCE "comportamiento_vial_id_comportamiento_seq" RENAME TO "road_behavior_id_seq";
ALTER SEQUENCE "decision_id_decision_seq" RENAME TO "decision_id_seq";
ALTER SEQUENCE "escenario_id_escenario_seq" RENAME TO "scenario_id_seq";
ALTER SEQUENCE "estado_simulacion_id_estado_simulacion_seq" RENAME TO "simulation_status_id_seq";
ALTER SEQUENCE "estado_usuario_id_estado_usuario_seq" RENAME TO "user_status_id_seq";
ALTER SEQUENCE "evaluacion_ia_id_evaluacion_ia_seq" RENAME TO "ai_evaluation_id_seq";
ALTER SEQUENCE "evento_vial_id_evento_vial_seq" RENAME TO "road_event_id_seq";
ALTER SEQUENCE "historial_acceso_id_historial_acceso_seq" RENAME TO "access_history_id_seq";
ALTER SEQUENCE "infraccion_id_infraccion_seq" RENAME TO "infraction_id_seq";
ALTER SEQUENCE "metrica_desempeno_id_metrica_seq" RENAME TO "performance_metric_id_seq";
ALTER SEQUENCE "modelo_ia_id_modelo_ia_seq" RENAME TO "ai_model_id_seq";
ALTER SEQUENCE "nivel_dificultad_id_nivel_dificultad_seq" RENAME TO "difficulty_level_id_seq";
ALTER SEQUENCE "nivel_gravedad_id_nivel_gravedad_seq" RENAME TO "severity_level_id_seq";
ALTER SEQUENCE "progreso_simulacion_id_progreso_seq" RENAME TO "simulation_progress_id_seq";
ALTER SEQUENCE "regla_transito_id_regla_transito_seq" RENAME TO "traffic_rule_id_seq";
ALTER SEQUENCE "retroalimentacion_id_retroalimentacion_seq" RENAME TO "feedback_id_seq";
ALTER SEQUENCE "rol_id_rol_seq" RENAME TO "role_id_seq";
ALTER SEQUENCE "sesion_entrenamiento_id_sesion_seq" RENAME TO "training_session_id_seq";
ALTER SEQUENCE "simulacion_id_simulacion_seq" RENAME TO "simulation_id_seq";
ALTER SEQUENCE "tipo_clima_id_tipo_clima_seq" RENAME TO "weather_type_id_seq";
ALTER SEQUENCE "tipo_evento_id_tipo_evento_seq" RENAME TO "event_type_id_seq";
ALTER SEQUENCE "tipo_metrica_id_tipo_metrica_seq" RENAME TO "metric_type_id_seq";
ALTER SEQUENCE "tipo_vehiculo_id_tipo_vehiculo_seq" RENAME TO "vehicle_type_id_seq";
ALTER SEQUENCE "tipo_via_id_tipo_via_seq" RENAME TO "road_type_id_seq";
ALTER SEQUENCE "usuario_id_usuario_seq" RENAME TO "users_id_seq";
ALTER SEQUENCE "vehiculo_id_vehiculo_seq" RENAME TO "vehicle_id_seq";

-- Recreacion de 14 funciones y procedimientos

CREATE OR REPLACE FUNCTION public.fn_auditar_cambios()
RETURNS TRIGGER AS $$
DECLARE
    v_antiguo JSONB;
    v_nuevo JSONB;
    v_usuario_app VARCHAR;
BEGIN
    -- Capturar el users de la aplicacion si Spring Boot lo configuro en la sesion local
    -- Usamos current_setting con true para que no lance error si no existe la variable
    v_usuario_app := current_setting('sbvia.app_user', true);
    IF v_usuario_app = '' THEN
        v_usuario_app := NULL;
    END IF;

    IF (TG_OP = 'DELETE') THEN
        v_antiguo := row_to_json(OLD)::JSONB;
        v_nuevo := NULL;
        
        INSERT INTO public.audit_log (
            table_name, operation, db_user, app_user, previous_data, new_data
        ) VALUES (
            TG_TABLE_NAME::TEXT, 'DELETE', current_user, v_usuario_app, v_antiguo, v_nuevo
        );
        RETURN OLD;
        
    ELSIF (TG_OP = 'UPDATE') THEN
        v_antiguo := row_to_json(OLD)::JSONB;
        v_nuevo := row_to_json(NEW)::JSONB;
        
        -- Solo auditar si hubo un cambio real
        IF v_antiguo IS DISTINCT FROM v_nuevo THEN
            INSERT INTO public.audit_log (
                table_name, operation, db_user, app_user, previous_data, new_data
            ) VALUES (
                TG_TABLE_NAME::TEXT, 'UPDATE', current_user, v_usuario_app, v_antiguo, v_nuevo
            );
        END IF;
        RETURN NEW;
        
    ELSIF (TG_OP = 'INSERT') THEN
        v_antiguo := NULL;
        v_nuevo := row_to_json(NEW)::JSONB;
        
        INSERT INTO public.audit_log (
            table_name, operation, db_user, app_user, previous_data, new_data
        ) VALUES (
            TG_TABLE_NAME::TEXT, 'INSERT', current_user, v_usuario_app, v_antiguo, v_nuevo
        );
        RETURN NEW;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP ROUTINE IF EXISTS public.sp_actualizar_usuarios_inactivos(date);

CREATE OR REPLACE PROCEDURE sp_actualizar_usuarios_inactivos(p_fecha_limite DATE, OUT actualizados INTEGER)
AS $$
BEGIN
    UPDATE "users"
    SET "status" = 'Inactivo',
        "active" = false,
        "actualizado_en" = CURRENT_TIMESTAMP
    WHERE "registered_at" < p_fecha_limite
      AND "active" = true;
      
    GET DIAGNOSTICS actualizados = ROW_COUNT;
END;
$$ LANGUAGE plpgsql;

DROP ROUTINE IF EXISTS public.sp_calcular_promedio_usuario(integer);

CREATE OR REPLACE FUNCTION sp_calcular_promedio_usuario(p_id_usuario INTEGER, OUT promedio DECIMAL)
AS $$
BEGIN
    SELECT COALESCE(AVG(s."final_score"), 0)
    INTO promedio
    FROM "simulation" s
    WHERE s."user_id" = p_id_usuario
      AND s."status" = 'Completado';
END;
$$ LANGUAGE plpgsql;

DROP ROUTINE IF EXISTS public.sp_calcular_puntaje_simulacion(integer);

CREATE OR REPLACE PROCEDURE sp_calcular_puntaje_simulacion(
    p_id_simulacion IN integer
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_penalizacion_total decimal := 0;
    v_puntaje_base decimal := 100.0;
    v_puntaje_final decimal;
BEGIN
    -- Sumar las penalizaciones de todas las infracciones de la simulacin
    SELECT COALESCE(SUM(penalizacion), 0) INTO v_penalizacion_total
    FROM "infraction"
    WHERE "simulation_id" = p_id_simulacion;

    -- Calcular puntaje final (mnimo 0)
    v_puntaje_final := v_puntaje_base - v_penalizacion_total;
    IF v_puntaje_final < 0 THEN
        v_puntaje_final := 0;
    END IF;

    -- Actualizar la simulacin
    UPDATE "simulation"
    SET "final_score" = v_puntaje_final,
        "status" = 'FINALIZADA'
    WHERE "simulation_id" = p_id_simulacion;
END;
$$;

DROP ROUTINE IF EXISTS public.sp_cerrar_simulaciones_vencidas(date);

CREATE OR REPLACE PROCEDURE sp_cerrar_simulaciones_vencidas(
    IN p_fecha_corte date,
    OUT p_actualizadas integer
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE "simulation"
       SET status = 'VENCIDA'
     WHERE status = 'EN_PROGRESO' AND ended_at < p_fecha_corte;
    GET DIAGNOSTICS p_actualizadas = ROW_COUNT;
END;
$$;

DROP ROUTINE IF EXISTS public.sp_generar_codigo_certificado(integer);

CREATE OR REPLACE FUNCTION sp_generar_codigo_certificado(p_id_simulacion INTEGER, OUT codigo_certificado VARCHAR)
AS $$
BEGIN
    codigo_certificado := 'CERT-' || EXTRACT(YEAR FROM CURRENT_DATE) || '-' || p_id_simulacion || '-' || nextval('seq_certificado');
END;
$$ LANGUAGE plpgsql;

DROP ROUTINE IF EXISTS public.sp_generar_codigo_reporte(integer);

CREATE OR REPLACE PROCEDURE sp_generar_codigo_reporte(
    IN p_id_reporte integer,
    OUT p_codigo varchar
)
LANGUAGE plpgsql AS $$
BEGIN
    p_codigo := 'REP-' || LPAD(p_id_reporte::text, 8, '0');
END;
$$;

DROP ROUTINE IF EXISTS public.sp_generar_reporte_simulacion(integer);

CREATE OR REPLACE PROCEDURE sp_generar_reporte_simulacion(IN p_id_simulacion integer)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM "simulation" WHERE "simulation_id" = p_id_simulacion) THEN
        RAISE EXCEPTION 'simulation % no encontrada', p_id_simulacion;
    END IF;
    INSERT INTO "Reporte" (tipo_reporte, generated_at, notes, "simulation_id")
    VALUES ('Evaluacion automatica', CURRENT_DATE, 'Reporte generado por el sistema', p_id_simulacion);
END;
$$;

DROP ROUTINE IF EXISTS public.sp_reporte_actividad_diaria(date);

CREATE OR REPLACE FUNCTION sp_reporte_actividad_diaria(p_fecha DATE)
RETURNS TABLE (
    total_simulaciones BIGINT,
    promedio_puntaje DECIMAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*),
        COALESCE(AVG("final_score"), 0)
    FROM "simulation"
    WHERE "started_at" = p_fecha;
END;
$$ LANGUAGE plpgsql;

DROP ROUTINE IF EXISTS public.sp_reporte_simulacion(integer);

CREATE OR REPLACE FUNCTION sp_reporte_simulacion(p_id_simulacion INTEGER)
RETURNS TABLE (
    simulacion_id INTEGER,
    usuario_nombre VARCHAR,
    escenario_nombre VARCHAR,
    final_score DECIMAL,
    simulation_status VARCHAR,
    tiempo_reaccion DECIMAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s."simulation_id",
        u."name",
        e."name",
        s."final_score",
        s."status",
        m."tiempo_reaccion"
    FROM "simulation" s
    INNER JOIN "users" u ON s."user_id" = u."user_id"
    INNER JOIN "scenario" e ON s."id" = e."id"
    LEFT JOIN "performance_metric" m ON s."simulation_id" = m."simulation_id"
    WHERE s."simulation_id" = p_id_simulacion;
END;
$$ LANGUAGE plpgsql;

DROP ROUTINE IF EXISTS public.sp_resumen_usuario(integer);

CREATE OR REPLACE PROCEDURE sp_resumen_usuario(
    IN p_id_usuario integer,
    OUT p_total_simulaciones integer,
    OUT p_promedio_puntaje numeric
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT COUNT(s."simulation_id"), COALESCE(AVG(s.final_score), 0)
      INTO p_total_simulaciones, p_promedio_puntaje
      FROM "users" u
      LEFT JOIN "simulation" s ON s."user_id" = u."user_id"
     WHERE u."user_id" = p_id_usuario;
END;
$$;

DROP ROUTINE IF EXISTS public.sp_validar_escenario(integer);

CREATE OR REPLACE FUNCTION sp_validar_escenario(p_id_escenario INTEGER, OUT es_valido BOOLEAN)
AS $$
DECLARE
    v_num_reglas INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO v_num_reglas
    FROM "traffic_rule"
    WHERE "id" = p_id_escenario;
    
    IF v_num_reglas >= 2 THEN
        es_valido := TRUE;
    ELSE
        es_valido := FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

DROP ROUTINE IF EXISTS public.sp_validar_simulacion(integer);

CREATE OR REPLACE PROCEDURE sp_validar_simulacion(
    IN p_id_simulacion integer,
    OUT p_valida boolean
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT EXISTS (
        SELECT 1 FROM "simulation" s
        JOIN "users" u ON u."user_id" = s."user_id"
        JOIN "scenario" e ON e."id" = s."id"
        WHERE s."simulation_id" = p_id_simulacion AND u.active = true AND e.active = true
    ) INTO p_valida;
END;
$$;

DROP ROUTINE IF EXISTS public.sp_actualizar_usuarios_inactivos(date);

CREATE OR REPLACE PROCEDURE sp_actualizar_usuarios_inactivos(p_fecha_limite DATE, OUT actualizados INTEGER)
AS $$
BEGIN
    -- Desactivar usuarios cuyo last_access_at supera el limite (inactividad real)
    -- Si last_access_at es NULL se usa registered_at como fallback conservador
    UPDATE "users"
    SET "active" = false
    WHERE COALESCE("last_access_at"::date, "registered_at") < p_fecha_limite
      AND "active" = true;

    GET DIAGNOSTICS actualizados = ROW_COUNT;

    -- Registrar en audit_log la operation masiva
    INSERT INTO public.audit_log (table_name, operation, db_user, new_data)
    VALUES (
        'users',
        'UPDATE',
        current_user,
        jsonb_build_object(
            'accion', 'INACTIVACION_MASIVA',
            'usuarios_desactivados', actualizados,
            'fecha_limite', p_fecha_limite
        )
    );
END;
$$ LANGUAGE plpgsql;
