-- N4 fix: usa ultimo_acceso en lugar de fecha_registro y escribe en bitacora_auditoria
CREATE OR REPLACE PROCEDURE sp_actualizar_usuarios_inactivos(p_fecha_limite DATE, OUT actualizados INTEGER)
AS $$
BEGIN
    -- Desactivar usuarios cuyo ultimo_acceso supera el limite (inactividad real)
    -- Si ultimo_acceso es NULL se usa fecha_registro como fallback conservador
    UPDATE "usuario"
    SET "activo" = false
    WHERE COALESCE("ultimo_acceso"::date, "fecha_registro") < p_fecha_limite
      AND "activo" = true;

    GET DIAGNOSTICS actualizados = ROW_COUNT;

    -- Registrar en bitacora_auditoria la operacion masiva
    INSERT INTO public.bitacora_auditoria (nombre_tabla, operacion, usuario_db, datos_nuevos)
    VALUES (
        'usuario',
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
