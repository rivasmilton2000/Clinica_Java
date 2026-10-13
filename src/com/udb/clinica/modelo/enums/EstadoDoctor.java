// ============================================================
// PAQUETE: com.udb.clinica.modelo.enums
// CLASE:   EstadoDoctor (enum)
//
// ¿QUÉ HACE?
// Define los 2 estados administrativos posibles del doctor.
// ============================================================

package com.udb.clinica.modelo.enums;

/**
 * ENUM: Estado administrativo del doctor.
 *   - ACTIVO   → puede atender citas.
 *   - INACTIVO → no disponible (vacaciones, baja, etc.).
 */
public enum EstadoDoctor {

    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String etiqueta;

    EstadoDoctor(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoDoctor desdeBD(String valor) {
        for (EstadoDoctor e : values()) {
            if (e.etiqueta.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de doctor inválido: " + valor);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

//Nestor Osegueda