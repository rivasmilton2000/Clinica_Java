// ============================================================
// PAQUETE: com.udb.clinica.modelo.enums
// CLASE:   EstadoCita (enum)
//
// ¿QUÉ HACE?
// Define los 3 estados posibles de una cita médica.
//
// ¿POR QUÉ IMPORTA?
// En la BD hay un índice único filtrado que impide que un doctor
// tenga 2 citas NO canceladas a la misma fecha y hora.
// Por eso CANCELADA es un estado clave para "liberar" el horario.
// ============================================================

package com.udb.clinica.modelo.enums;

/**
 * ENUM: Estados posibles de una cita médica.
 *   - PROGRAMADA → agendada, aún no atendida.
 *   - ATENDIDA   → el doctor ya la atendió.
 *   - CANCELADA  → se canceló (libera el horario del doctor).
 */
public enum EstadoCita {

    PROGRAMADA("Programada"),
    ATENDIDA("Atendida"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoCita(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoCita desdeBD(String valor) {
        for (EstadoCita e : values()) {
            if (e.etiqueta.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de cita inválido: " + valor);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

//Nestor Osegueda