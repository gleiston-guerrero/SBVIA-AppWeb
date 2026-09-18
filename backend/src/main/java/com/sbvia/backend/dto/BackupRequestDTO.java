package com.sbvia.backend.dto;

import java.time.LocalDateTime;

/**
 * <p>BackupRequestDTO class.</p>
 *
 * @author Keitho_
 */
public class BackupRequestDTO {    private String modalidad;
    private LocalDateTime fechaProgramada;
    private String comentario;

    /**
     * Método público.
     *
     * @return a {@link java.lang.String} object
     */
    public String getModalidad() {
        return modalidad;
    }

    /**
     * Método público.
     *
     * @param modalidad a {@link java.lang.String} object
     */
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    /**
     * Método público.
     *
     * @return a {@link java.time.LocalDateTime} object
     */
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * Método público.
     *
     * @param fechaProgramada a {@link java.time.LocalDateTime} object
     */
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * Método público.
     *
     * @return a {@link java.lang.String} object
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Método público.
     *
     * @param comentario a {@link java.lang.String} object
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
