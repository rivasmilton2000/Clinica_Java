// ============================================================
// PAQUETE: com.udb.clinica.modelo.enums
// CLASE:   GravedadPaciente (enum)
//
// ¿QUÉ HACE?
// Define los 3 niveles de gravedad clínica del paciente.
//
// ⚠️ ¿EN QUÉ SE DIFERENCIA DE EstadoPaciente?
// - EstadoPaciente   → administrativo (Activo/Inactivo).
// - GravedadPaciente → clínico (Leve/Moderado/Grave).
// 
// Los DOS coexisten: un paciente puede estar ACTIVO y GRAVE.
// ============================================================

package com.udb.clinica.modelo.enums;

/**
 * ENUM: Gravedad clínica del paciente.
 *   - LEVE     → no urgente.
 *   - MODERADO → requiere atención pronta.
 *   - GRAVE    → requiere atención inmediata.
 */
public enum GravedadPaciente {

    LEVE("Leve"),
    MODERADO("Moderado"),
    GRAVE("Grave");

    private final String etiqueta;

    GravedadPaciente(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static GravedadPaciente desdeBD(String valor) {
        for (GravedadPaciente g : values()) {
            if (g.etiqueta.equalsIgnoreCase(valor)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Gravedad inválida: " + valor);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

//Nestor Osegueda