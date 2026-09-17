-- Rename bitacora_auditoria to audit_log
ALTER TABLE bitacora_auditoria RENAME TO audit_log;
ALTER TABLE audit_log RENAME COLUMN id_auditoria TO id;
ALTER TABLE audit_log RENAME COLUMN nombre_tabla TO table_name;
ALTER TABLE audit_log RENAME COLUMN operacion TO operation;
ALTER TABLE audit_log RENAME COLUMN usuario_db TO db_user;
ALTER TABLE audit_log RENAME COLUMN usuario_app TO app_user;
ALTER TABLE audit_log RENAME COLUMN fecha_hora TO created_at;
ALTER TABLE audit_log RENAME COLUMN datos_anteriores TO previous_data;
ALTER TABLE audit_log RENAME COLUMN datos_nuevos TO new_data;
ALTER SEQUENCE bitacora_auditoria_id_auditoria_seq RENAME TO audit_log_id_seq;

-- Rename respaldo to backup
ALTER TABLE respaldo RENAME TO backup;
ALTER TABLE backup RENAME COLUMN id_respaldo TO id;
ALTER TABLE backup RENAME COLUMN nombre_archivo TO file_name;
ALTER TABLE backup RENAME COLUMN tipo TO type;
ALTER TABLE backup RENAME COLUMN estado TO status;
ALTER TABLE backup RENAME COLUMN fecha_inicio TO start_date;
ALTER TABLE backup RENAME COLUMN fecha_fin TO end_date;
ALTER TABLE backup RENAME COLUMN fecha_programada TO scheduled_date;
ALTER TABLE backup RENAME COLUMN tamanio_bytes TO size_bytes;
ALTER TABLE backup RENAME COLUMN modalidad TO mode;
ALTER TABLE backup RENAME COLUMN comentario TO comment;
ALTER TABLE backup RENAME COLUMN detalles TO details;
ALTER SEQUENCE respaldo_id_respaldo_seq RENAME TO backup_id_seq;
