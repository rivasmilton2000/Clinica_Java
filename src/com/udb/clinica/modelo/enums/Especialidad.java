// ============================================================
// PAQUETE: com.udb.clinica.modelo.enums
// CLASE:   Especialidad (enum)
//
// ¿QUÉ HACE?
// Define las 4 especialidades médicas permitidas.
//
// ¿POR QUÉ EXISTE?
// Refleja el CHECK de la columna 'especialidad' en la tabla 'doctor'.
// ============================================================

package com.udb.clinica.modelo.enums;

/**
 * ENUM: Especialidades médicas permitidas en la clínica.
 */
public enum Especialidad {

    // Cada constante con su etiqueta legible
    MEDICINA_GENERAL("Medicina General"),
    PSICOLOGIA("Psicología"),
    NUTRICION("Nutrición"),
    FISIOTERAPIA("Fisioterapia");

    // Atributo inmutable con el texto exacto para la BD
    private final String etiqueta;

    // Constructor (privado automáticamente)
    Especialidad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    // Getter de la etiqueta
    public String getEtiqueta() {
        return etiqueta;
    }

    // Convierte un String de la BD al enum
    public static Especialidad desdeBD(String valor) {
        for (Especialidad e : values()) {
            if (e.etiqueta.equalsIgnoreCase(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Especialidad inválida: " + valor);
    }

    // Se imprime la etiqueta (no el nombre interno)
    @Override
    public String toString() {
        return etiqueta;
    }
}

//Nestor Osegueda