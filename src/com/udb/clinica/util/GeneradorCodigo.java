// ============================================================
// PAQUETE: com.udb.clinica.util
// CLASE:   GeneradorCodigo
//
// ¿QUÉ HACE?
// Genera códigos correlativos automáticos consultando la BD:
//   - siguienteCodigoPaciente() → "P005"
//   - siguienteCodigoDoctor()   → "D003"
//   - siguienteCodigoCita()     → "C012"
//
// ¿POR QUÉ EXISTE?
// - Evita códigos duplicados (PK garantizada).
// - Formato consistente (X###, con ceros a la izquierda).
// - El usuario NO escribe el código (campo deshabilitado en UI).
//
// ¿CÓMO FUNCIONA?
// 1. SELECT MAX(codigo) FROM tabla
// 2. Si null → es el primero → "P001"
// 3. Si 'P004' → extraer 4, sumar 1 → "P005"
// 4. Formatear con String.format("%03d")
//
// ¿POR QUÉ NO CIERRA LA CONEXIÓN?
// Porque usamos el patrón Singleton en ConexionBD.
// La conexión es única y compartida por toda la app.
// Solo cerramos PreparedStatement y ResultSet (que son por consulta).
// ============================================================

package com.udb.clinica.util;

import com.udb.clinica.conexion.ConexionBD;
import com.udb.clinica.excepciones.ClinicaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Genera códigos correlativos consultando la base de datos.
 * 
 * <p>Los códigos siguen el formato: [PREFIJO][NÚMERO CON CEROS]</p>
 * <ul>
 *   <li>Paciente → P001, P002, ..., P999, P1000</li>
 *   <li>Doctor   → D001, D002, ...</li>
 *   <li>Cita     → C001, C002, ...</li>
 * </ul>
 * 
 * <p><b>Importante:</b> el generador NO reutiliza huecos. Si se borra
 * el registro P003, el siguiente código será el que sigue al máximo
 * actual (por ejemplo, P005 si el máximo es P004).</p>
 */
public class GeneradorCodigo {

    // ========================================================
    // CONSTANTES DE PREFIJOS
    // ========================================================
    // Los prefijos para cada tipo de entidad.
    // static final = constante de clase, no cambia.
    // ========================================================
    private static final String PREFIJO_PACIENTE = "P";
    private static final String PREFIJO_DOCTOR   = "D";
    private static final String PREFIJO_CITA     = "C";

    // ========================================================
    // CONSTRUCTOR PRIVADO (Utility Class)
    // ========================================================
    // Evita que alguien haga "new GeneradorCodigo()".
    // Solo se usan los métodos estáticos.
    // ========================================================
    private GeneradorCodigo() {
        // No se instancia.
    }

    // ========================================================
    // MÉTODOS PÚBLICOS (API para el resto del sistema)
    // ========================================================

    /**
     * Devuelve el próximo código de paciente.
     * 
     * @return String tipo "P005"
     * @throws ClinicaException si falla la consulta a la BD
     */
    public static String siguienteCodigoPaciente() throws ClinicaException {
        return siguienteCodigo("paciente", "codigo_paciente", PREFIJO_PACIENTE);
    }

    /**
     * Devuelve el próximo código de doctor.
     * 
     * @return String tipo "D003"
     * @throws ClinicaException si falla la consulta a la BD
     */
    public static String siguienteCodigoDoctor() throws ClinicaException {
        return siguienteCodigo("doctor", "codigo_doctor", PREFIJO_DOCTOR);
    }

    /**
     * Devuelve el próximo código de cita.
     * 
     * @return String tipo "C012"
     * @throws ClinicaException si falla la consulta a la BD
     */
    public static String siguienteCodigoCita() throws ClinicaException {
        return siguienteCodigo("cita", "codigo_cita", PREFIJO_CITA);
    }

    // ========================================================
    // MÉTODO GENÉRICO INTERNO
    // ========================================================
    // Los 3 métodos públicos delegan a este, pasando los
    // nombres de tabla/columna y el prefijo.
    // 
    // Así evitamos repetir la lógica 3 veces (principio DRY:
    // "Don't Repeat Yourself").
    // 
    // ⚠️ SEGURIDAD:
    // Los nombres de tabla/columna vienen de CONSTANTES internas,
    // NUNCA del usuario. Por eso es seguro concatenarlos en SQL.
    // Si vinieran del usuario, sería SQL Injection.
    // ========================================================
    private static String siguienteCodigo(String tabla, String columna, String prefijo)
            throws ClinicaException {

        // Construir la consulta SQL.
        // Ejemplo: "SELECT MAX(codigo_paciente) AS maxCodigo FROM paciente"
        String sql = "SELECT MAX(" + columna + ") AS maxCodigo FROM " + tabla;

        try {
            // --------------------------------------------------
            // ⚠️ IMPORTANTE: la Connection NO se cierra aquí.
            // --------------------------------------------------
            // ¿Por qué?
            // Porque usamos el patrón Singleton en ConexionBD.
            // La conexión es única y se reutiliza por toda la app.
            // Si la cerráramos, el Singleton tendría que recrearla
            // en cada llamada (que es justo lo que queremos evitar).
            // --------------------------------------------------
            Connection con = ConexionBD.getConexion();

            // PreparedStatement y ResultSet SÍ se cierran automáticamente
            // con try-with-resources, porque son recursos por consulta.
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                // ----------------------------------------------
                // Leer el resultado de MAX()
                // ----------------------------------------------
                if (rs.next()) {
                    String maxCodigo = rs.getString("maxCodigo");

                    // ------------------------------------------
                    // Caso 1: la tabla está vacía → primer código.
                    // ------------------------------------------
                    // MAX() devuelve NULL si no hay filas.
                    // ------------------------------------------
                    if (maxCodigo == null || maxCodigo.isEmpty()) {
                        LogUtil.info("Generando primer código para " + tabla + ": " + prefijo + "001");
                        return prefijo + "001";
                    }

                    // ------------------------------------------
                    // Caso 2: ya hay códigos → sumar 1.
                    // ------------------------------------------
                    // Ejemplo: "P004" → "004" → 4 → 5 → "P005"
                    // ------------------------------------------
                    // substring(prefijo.length()) → corta el prefijo
                    // Integer.parseInt(...)       → convierte a número
                    // String.format("%03d", n)    → formatea con ceros
                    // ------------------------------------------
                    String numeroTexto = maxCodigo.substring(prefijo.length());
                    int numero         = Integer.parseInt(numeroTexto);
                    int siguiente      = numero + 1;
                    String nuevoCodigo = prefijo + String.format("%03d", siguiente);

                    LogUtil.info("Código generado para " + tabla + ": " + nuevoCodigo);
                    return nuevoCodigo;

                } else {
                    // No debería pasar (MAX siempre devuelve 1 fila),
                    // pero por seguridad devolvemos el primer código.
                    LogUtil.warn("La consulta MAX no devolvió filas para " + tabla);
                    return prefijo + "001";
                }
            }

        } catch (SQLException e) {
            // Error de BD al consultar MAX.
            LogUtil.error("Error al generar código para " + tabla + ": " + e.getMessage(), e);
            throw new ClinicaException(
                    "No se pudo generar el código de " + tabla + ": " + e.getMessage(), e);
        }
    }
}