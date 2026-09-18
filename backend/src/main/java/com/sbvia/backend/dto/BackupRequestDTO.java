package com.sbvia.backend.dto;

import java.time.LocalDateTime;

public class BackupRequestDTO {
    private String modalidad;
    private LocalDateTime fechaProgramada;
    private String comentario;

    /**
     * Método público.
     */
    public String getModalidad() {
        return modalidad;
    }

    /**
     * Método público.
     */
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    /**
     * Método público.
     */
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * Método público.
     */
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * Método público.
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Método público.
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
