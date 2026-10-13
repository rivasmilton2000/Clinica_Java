// ============================================================
// PAQUETE: com.udb.clinica.util
// CLASE:   Validador
//
// ¿QUÉ HACE?
// Centraliza TODAS las validaciones del sistema.
// 
// ¿POR QUÉ EXISTE?
// - Un solo lugar para las reglas de validación.
// - Reutilizable desde DAOs, vistas, y clases de prueba.
// - Fácil de mantener: cambia una regla = cambia un método.
// 
// ¿CÓMO SE USA?
//   if (!Validador.esCorreoValido(correo)) {
//       // error
//   }
//   Validador.requerirNoVacio(nombre, "Nombre");
// ============================================================

package com.udb.clinica.util;

// Importamos nuestra excepción para lanzarla cuando algo falla
import com.udb.clinica.excepciones.ClinicaException;

// Importamos Pattern y Matcher para las expresiones regulares (regex)
import java.util.regex.Pattern;

/**
 * Clase de utilidad con todas las validaciones del sistema.
 * 
 * <p>Métodos con prefijo {@code es...()} devuelven {@code boolean}
 * (no lanzan excepción).</p>
 * 
 * <p>Métodos con prefijo {@code requerir...()} lanzan
 * {@link ClinicaException} si la validación falla.</p>
 */
public class Validador {

    // ========================================================
    // EXPRESIONES REGULARES (REGEX)
    // ========================================================
    // Una regex es un "patrón de texto" que describe qué forma
    // debe tener una cadena para considerarse válida.
    // 
    // ¿Por qué 'static final'?
    // Porque se compilan UNA VEZ y se reutilizan. Mejor rendimiento.
    // ========================================================

    /**
     * Regex para correo electrónico.
     * 
     * Desglose:
     *   ^                   → inicio de la cadena
     *   [\w.-]+             → uno o más caracteres alfanuméricos, _ . -
     *   @                   → arroba literal
     *   [\w-]+              → dominio (letras, números, guion)
     *   (\.[\w-]+)+         → uno o más ".dominio"
     *   \.[a-zA-Z]{2,}      → ".com", ".sv", ".edu" (2+ letras)
     *   $                   → fin de la cadena
     * 
     * Ejemplos válidos:   juan@udb.edu.sv | maria.gomez@gmail.com
     * Ejemplos inválidos: juan@ | @udb.sv | juan@udb | juan.udb.sv
     */
    private static final Pattern PATRON_CORREO = 
        Pattern.compile("^[\\w]+(\\.[\\w]+)*@[\\w]+(\\.[\\w]+)*\\.[a-zA-Z]{2,}$");

    /**
     * Regex para teléfono salvadoreño con formato 7777-1234.
     * 
     * Desglose:
     *   ^         → inicio
     *   \d{4}     → exactamente 4 dígitos
     *   -         → guión literal (obligatorio)
     *   \d{4}     → exactamente 4 dígitos
     *   $         → fin
     * 
     * Ejemplos válidos:   7777-1234 | 2222-5678
     * Ejemplos inválidos: 77771234 | 777-1234 | 7777 1234
     */
    private static final Pattern PATRON_TELEFONO = 
            Pattern.compile("^\\d{4}-\\d{4}$");

    // ========================================================
    // CONSTRUCTOR PRIVADO
    // ========================================================
    // Utility Class: no se instancia, solo se usan sus métodos static.
    // ========================================================
    private Validador() {
        // No se usa.
    }

    // ========================================================
    // VALIDACIONES QUE DEVUELVEN boolean
    // ========================================================
    // Estas no lanzan excepción: solo dicen sí o no.
    // Útiles para chequeos rápidos.
    // ========================================================

    /**
     * Verifica si un correo tiene formato válido.
     * 
     * @param correo el correo a validar
     * @return true si es válido, false en caso contrario (o si es null)
     */
    public static boolean esCorreoValido(String correo) {
        // Primero: no null y no vacío
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        // Segundo: cumple el patrón
        return PATRON_CORREO.matcher(correo.trim()).matches();
    }

    /**
     * Verifica si un teléfono tiene formato 7777-1234.
     * 
     * @param telefono el teléfono a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        return PATRON_TELEFONO.matcher(telefono.trim()).matches();
    }

    /**
     * Verifica si una edad está en el rango permitido (1 a 119).
     * 
     * @param edad la edad a validar
     * @return true si está en rango
     */
    public static boolean esEdadValida(int edad) {
        return edad > 0 && edad < 120;
    }

    /**
     * Verifica si una cadena es nula o vacía.
     * 
     * @param texto el texto a verificar
     * @return true si es null o solo espacios
     */
    public static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // ========================================================
    // VALIDACIONES QUE LANZAN EXCEPCIÓN
    // ========================================================
    // Útiles cuando un dato inválido IMPIDE continuar.
    // Lanzan ClinicaException con mensaje claro.
    // ========================================================

    /**
     * Verifica que un texto NO sea vacío. Si lo es, lanza excepción.
     * 
     * @param texto     el texto a verificar
     * @param nombreCampo el nombre del campo (para el mensaje)
     * @throws ClinicaException si está vacío
     */
    public static void requerirNoVacio(String texto, String nombreCampo) 
            throws ClinicaException {
        if (esVacio(texto)) {
            throw new ClinicaException("El campo '" + nombreCampo + "' no puede estar vacío.");
        }
    }

    /**
     * Verifica que un correo sea válido. Si no, lanza excepción.
     * 
     * @param correo el correo a verificar
     * @throws ClinicaException si no es válido
     */
    public static void requerirCorreoValido(String correo) 
            throws ClinicaException {
        if (!esCorreoValido(correo)) {
            throw new ClinicaException(
                "Correo inválido: '" + correo + "'. Formato esperado: usuario@dominio.com");
        }
    }

    /**
     * Verifica que un teléfono sea válido. Si no, lanza excepción.
     * 
     * @param telefono el teléfono a verificar
     * @throws ClinicaException si no es válido
     */
    public static void requerirTelefonoValido(String telefono) 
            throws ClinicaException {
        if (!esTelefonoValido(telefono)) {
            throw new ClinicaException(
                "Teléfono inválido: '" + telefono + "'. Formato esperado: 7777-1234");
        }
    }

    /**
     * Verifica que una edad sea válida. Si no, lanza excepción.
     * 
     * @param edad la edad a verificar
     * @throws ClinicaException si no está en rango
     */
    public static void requerirEdadValida(int edad) 
            throws ClinicaException {
        if (!esEdadValida(edad)) {
            throw new ClinicaException(
                "Edad inválida: " + edad + ". Debe estar entre 1 y 119.");
        }
    }

    // ========================================================
    // MÉTODOS DE FORMATO / AYUDA
    // ========================================================

    /**
     * Devuelve el ejemplo del formato de teléfono esperado.
     * Útil para mostrar en tooltips y mensajes de ayuda.
     */
    public static String ejemploTelefono() {
        return "7777-1234";
    }

    /**
     * Devuelve el ejemplo del formato de correo esperado.
     */
    public static String ejemploCorreo() {
        return "usuario@dominio.com";
    }
}