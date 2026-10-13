// ============================================================
// PAQUETE: com.udb.clinica.modelo.enums
// CLASE:   TipoPaciente (enum)
// 
// ¿QUÉ HACE?
// Define los 4 tipos válidos de paciente.
// 
// ¿POR QUÉ EXISTE?
// - Evita errores de escritura ("Estudiante" vs "estudiante").
// - Refleja el CHECK de la tabla 'paciente' en la BD.
// - El compilador valida los valores, no el programador.
// ============================================================

package com.udb.clinica.modelo.enums;

/**
 * ENUM: Tipos de paciente permitidos en la Clínica Universitaria Vida UDB.
 * 
 * Solo pueden existir estos 4 valores:
 *   - ESTUDIANTE
 *   - DOCENTE
 *   - ADMINISTRATIVO
 *   - VISITANTE
 * 
 * Cada constante tiene asociada una "etiqueta" (String) que es el texto
 * exacto que se guarda en la base de datos.
 */
public enum TipoPaciente {

    // --------------------------------------------------------
    // CONSTANTES DEL ENUM
    // --------------------------------------------------------
    // Cada constante se define así: NOMBRE_INTERNO("etiqueta legible").
    // El valor entre paréntesis se pasa al constructor.
    // 
    // ⚠️ IMPORTANTE:
    // - Los nombres internos (ESTUDIANTE) van en MAYÚSCULAS por convención.
    // - La etiqueta ("Estudiante") es lo que ve el usuario y lo que 
    //   se guarda en la BD.
    // --------------------------------------------------------
    ESTUDIANTE("Estudiante"),
    DOCENTE("Docente"),
    ADMINISTRATIVO("Administrativo"),
    VISITANTE("Visitante");

    // --------------------------------------------------------
    // ATRIBUTO
    // --------------------------------------------------------
    // 'final' significa que una vez asignado NO se puede cambiar.
    // Cada constante del enum tiene su propia etiqueta.
    // --------------------------------------------------------
    private final String etiqueta;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------
    // Los enums tienen constructor PRIVADO automáticamente.
    // No se puede llamar desde fuera, solo lo usa el propio enum.
    // Se ejecuta UNA sola vez por cada constante cuando carga la clase.
    // --------------------------------------------------------
    TipoPaciente(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    // --------------------------------------------------------
    // GETTER
    // --------------------------------------------------------
    // Permite leer la etiqueta desde otras clases.
    // Ejemplo: TipoPaciente.ESTUDIANTE.getEtiqueta() → "Estudiante"
    // --------------------------------------------------------
    public String getEtiqueta() {
        return etiqueta;
    }

    // --------------------------------------------------------
    // MÉTODO ESTÁTICO: desdeBD
    // --------------------------------------------------------
    // ¿QUÉ HACE?
    // Convierte un String (que viene de la BD) al enum de Java.
    // 
    // ¿POR QUÉ LO NECESITAMOS?
    // Cuando leemos de la BD, SQL Server nos devuelve "Estudiante"
    // (un String). Pero nosotros queremos un TipoPaciente.
    // Este método hace la conversión.
    // 
    // ¿CÓMO FUNCIONA?
    // 1. Recorremos todos los valores del enum (values()).
    // 2. Comparamos la etiqueta ignorando mayúsculas/minúsculas.
    // 3. Si hay coincidencia, devolvemos ese enum.
    // 4. Si NO hay coincidencia, lanzamos excepción (dato corrupto).
    // --------------------------------------------------------
    public static TipoPaciente desdeBD(String valor) {
        for (TipoPaciente t : values()) {
            if (t.etiqueta.equalsIgnoreCase(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de paciente inválido: " + valor);
    }

    // --------------------------------------------------------
    // toString()
    // --------------------------------------------------------
    // Por defecto, un enum se imprime con su nombre interno: "ESTUDIANTE".
    // Con este override, se imprime la etiqueta: "Estudiante".
    // 
    // Esto se usa en JComboBox, tablas y mensajes al usuario.
    // --------------------------------------------------------
    @Override
    public String toString() {
        return etiqueta;
    }
}

//Nestor Osegueda