package com.sbvia.backend.dto;

import java.time.LocalDateTime;

/**
 * Request body for creating a database backup.
 *
 * @author Keitho_
 */
public class BackupRequestDTO {    private String modalidad;
    private LocalDateTime fechaProgramada;
    private String comentario;

    /**
     * Returns the backup modality (e.g. manual or scheduled) configured for
     * this backup request.
     *
     * @return the backup modality as a string
     */
    public String getModalidad() {
        return modalidad;
    }

    /**
     * Sets the backup modality (e.g. manual or scheduled) for this backup
     * request.
     *
     * @param modalidad the backup modality to configure
     */
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    /**
     * Returns the date and time at which the backup is scheduled to run.
     *
     * @return the scheduled date and time of the backup
     */
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * Sets the date and time at which the backup is scheduled to run.
     *
     * @param fechaProgramada the date and time to schedule the backup for
     */
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * Returns the free-text comment attached to this backup request.
     *
     * @return the comment describing this backup request
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Sets the free-text comment attached to this backup request.
     *
     * @param comentario the comment describing this backup request
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
