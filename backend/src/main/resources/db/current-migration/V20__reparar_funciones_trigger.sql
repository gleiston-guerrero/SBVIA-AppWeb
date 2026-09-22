-- Repara las cinco funciones de trigger que V19 dejo apuntando a los nombres viejos.
--
-- Que paso. V19 renombro las 26 tablas y sus 192 columnas, y recreo las rutinas
-- almacenadas, pero las tomo de los archivos que las declaran con
-- "CREATE OR REPLACE". V1 es un volcado de pg_dump y declara sus funciones con
-- "CREATE FUNCTION public.x() RETURNS trigger" y el cuerpo despues de "AS $$",
-- forma que el patron de V19 no reconocio. Las cinco funciones de trigger de V1
-- se quedaron consultando tablas y columnas que ya no existen, y los disparadores
-- que las invocan fallaban. Comprobado contra el despliegue: iniciar una practica
-- respondia 500, porque trg_validar_usuario_sesion ejecuta fn_validar_usuario_sesion
-- y esa funcion consulta sesion_entrenamiento.
--
-- Por que esta version esta escrita a mano y no generada. Dentro de un cuerpo,
-- "id_simulacion" significa dos cosas segun donde aparezca: la clave primaria
-- dentro de la tabla simulacion, y la clave foranea dentro de una tabla que la
-- referencia. Un reemplazo ciego convierte NEW.id_simulacion en NEW.id, que no
-- existe, y WHERE id_simulacion en WHERE simulation_id sobre la propia tabla, que
-- tampoco. Se intento y el resultado era incorrecto, asi que se traduce cada
-- cuerpo leyendo contra que tabla consulta en cada punto.
--
-- No se modifica ninguna migracion ya aplicada: Flyway valida su checksum.

SET client_min_messages TO WARNING;

-- ---------------------------------------------------------------------------
-- 1. fn_actualizar_ultimo_acceso
--    Trigger AFTER INSERT sobre access_history (historial_acceso en V1).
--    NEW es una fila de access_history; users es otra tabla.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_actualizar_ultimo_acceso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.successful_login = TRUE THEN
        UPDATE users
        SET last_access_at = NEW.occurred_at,
            failed_attempts = 0
        WHERE id = NEW.user_id;
    ELSE
        UPDATE users
        SET failed_attempts = failed_attempts + 1
        WHERE id = NEW.user_id;
    END IF;

    RETURN NEW;
END;
$$;

-- ---------------------------------------------------------------------------
-- 2. fn_finalizar_simulacion_por_progreso
--    Trigger AFTER INSERT OR UPDATE OF percentage sobre simulation_progress.
--    Consulta simulation_status y actualiza simulation.
--    El valor 'COMPLETADA' es DATO del catalogo, no un nombre de objeto: V19
--    renombro el esquema, no las filas, asi que se conserva tal cual.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_finalizar_simulacion_por_progreso() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_completed_status_id INTEGER;
BEGIN
    IF NEW.percentage >= 100 THEN
        SELECT id
          INTO v_completed_status_id
          FROM simulation_status
         WHERE name = 'COMPLETADA'
         LIMIT 1;

        UPDATE simulation
           SET progress_percentage = 100,
               ended_at = COALESCE(ended_at, NEW.occurred_at),
               simulation_status_id = COALESCE(
                   v_completed_status_id,
                   simulation_status_id
               )
         WHERE id = NEW.simulation_id;
    END IF;

    RETURN NEW;
END;
$$;

-- ---------------------------------------------------------------------------
-- 3. fn_recalcular_puntaje_simulacion
--    Trigger AFTER INSERT OR DELETE OR UPDATE sobre infraction.
--    Recalcula el puntaje de la simulacion afectada.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_recalcular_puntaje_simulacion() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_simulation_id BIGINT;
    v_penalty NUMERIC(10,2);
BEGIN
    v_simulation_id := COALESCE(NEW.simulation_id, OLD.simulation_id);

    SELECT COALESCE(SUM(applied_penalty), 0)
      INTO v_penalty
      FROM infraction
     WHERE simulation_id = v_simulation_id;

    UPDATE simulation
       SET final_score = GREATEST(0, 100 - v_penalty)
     WHERE id = v_simulation_id;

    RETURN COALESCE(NEW, OLD);
END;
$$;

-- ---------------------------------------------------------------------------
-- 4. fn_sincronizar_progreso_simulacion
--    Trigger AFTER INSERT sobre simulation_progress.
--    Copia el porcentaje a la simulacion sin retrocederlo nunca.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_sincronizar_progreso_simulacion() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE simulation
    SET progress_percentage = GREATEST(progress_percentage, NEW.percentage)
    WHERE id = NEW.simulation_id;

    RETURN NEW;
END;
$$;

-- ---------------------------------------------------------------------------
-- 5. fn_validar_usuario_sesion
--    Trigger BEFORE INSERT OR UPDATE OF training_session_id, user_id ON simulation.
--    Exige que la sesion exista y que pertenezca al mismo usuario que la simulacion.
--    NEW es una fila de simulation; training_session es otra tabla.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_validar_usuario_sesion() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_session_user_id BIGINT;
BEGIN
    SELECT user_id
    INTO v_session_user_id
    FROM training_session
    WHERE id = NEW.training_session_id;

    IF v_session_user_id IS NULL THEN
        RAISE EXCEPTION 'Training session % does not exist.', NEW.training_session_id;
    END IF;

    IF v_session_user_id <> NEW.user_id THEN
        RAISE EXCEPTION
            'The user of the simulation must match the user of the training session.';
    END IF;

    RETURN NEW;
END;
$$;
