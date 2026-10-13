// ============================================================
// PAQUETE: com.udb.clinica.modelo.enums
// CLASE:   EstadoPaciente (enum)
//
// ¿QUÉ HACE?
// Define los 2 estados administrativos posibles del paciente.
//
// ⚠️ OJO: NO confundir con GravedadPaciente.
//    - EstadoPaciente    → ¿está activo en el sistema? (administrativo)
//    - GravedadPaciente  → ¿qué tan grave está? (clínico)
// ============================================================

package com.udb.clinica.modelo.enums;

/**
 * ENUM: Estado administrativo del paciente.
 *   - ACTIVO   → puede agendar citas.
 *   - INACTIVO → está dado de baja.
 */
public enum EstadoPaciente {

    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String etiqueta;

    EstadoPaciente(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoPaciente desdeBD(String valor) {
        for (EstadoPaciente e : values()) {
            if (e.etiqueta.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de paciente inválido: " + valor);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

//Nestor Osegueda