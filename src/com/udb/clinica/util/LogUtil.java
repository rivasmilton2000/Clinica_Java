// ============================================================
// PAQUETE: com.udb.clinica.util
// CLASE:   LogUtil
//
// ¿QUÉ HACE?
// Configura y expone un Logger de Java para toda la aplicación.
// Los mensajes se escriben en consola Y en el archivo clinica.log.
//
// ¿POR QUÉ EXISTE?
// - Centraliza la configuración del logging (un solo lugar).
// - Evita repetir configuración en cada clase.
// - Guarda un historial de todo lo que hace la app.
//
// ¿CÓMO SE USA?
//   LogUtil.info("Paciente insertado: P005");
//   LogUtil.error("Error al conectar", excepcion);
//   LogUtil.warn("Edad sospechosa: 150");
// ============================================================

package com.udb.clinica.util;

// Imports del sistema de logging de Java
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utilidad centralizada para el manejo de logs.
 * 
 * Escribe los mensajes en el archivo "clinica.log" ubicado en
 * la carpeta del proyecto, y también los muestra en consola.
 */
public class LogUtil {

    // ========================================================
    // CONSTANTES DE CONFIGURACIÓN
    // ========================================================
    // El nombre del logger. Por convención se usa el nombre de
    // la clase (o del proyecto). Aquí usamos uno global.
    // ========================================================
    private static final String NOMBRE_LOGGER = "ClinicaUDB";

    // Nombre del archivo donde se guardarán los logs.
    private static final String ARCHIVO_LOG = "clinica.log";

    // Tamaño máximo del archivo antes de rotarlo (5 MB = 5*1024*1024).
    // Cuando supere este tamaño, se crea un nuevo archivo.
    private static final int TAMANO_MAXIMO = 5 * 1024 * 1024;

    // Cuántos archivos rotados mantener (ej. clinica.log.1, .2, .3).
    // Cuando se llena uno, se crea otro y se borra el más viejo.
    private static final int ARCHIVOS_ROTADOS = 3;

    // ========================================================
    // INSTANCIA ÚNICA DEL LOGGER
    // ========================================================
    // static final → una sola instancia para toda la app.
    // Se inicializa la primera vez que se usa la clase.
    // ========================================================
    private static final Logger logger = Logger.getLogger(NOMBRE_LOGGER);

    // ========================================================
    // BLOQUE ESTÁTICO DE INICIALIZACIÓN
    // ========================================================
    // Este bloque se ejecuta UNA SOLA VEZ cuando la clase se carga
    // por primera vez en la JVM. Ideal para configuración.
    // 
    // Aquí configuramos el FileHandler (que escribe en archivo).
    // ========================================================
    static {
        try {
            // FileHandler: clase de Java que escribe logs a un archivo.
            // Parámetros:
            //   1. Patrón del archivo: "clinica.log"
            //   2. Tamaño máximo antes de rotar: 5 MB
            //   3. Cantidad de archivos rotados a mantener: 3
            //   4. append = true (agregar al final, no sobreescribir)
            FileHandler fileHandler = new FileHandler(
                    ARCHIVO_LOG, TAMANO_MAXIMO, ARCHIVOS_ROTADOS, true);

            // SimpleFormatter: formato de texto plano legible.
            // Alternativa: XMLFormatter (formato XML, menos legible).
            fileHandler.setFormatter(new SimpleFormatter());

            // Registrar el handler en el logger.
            // A partir de aquí, todos los mensajes van al archivo.
            logger.addHandler(fileHandler);

            // Nivel mínimo de mensajes que se registran.
            // ALL < FINEST < FINER < FINE < CONFIG < INFO < WARNING < SEVERE < OFF
            // Usamos INFO: registra INFO, WARNING y SEVERE.
            logger.setLevel(Level.INFO);

            // Evitar que los mensajes se propaguen al logger padre
            // (que los mostraría doble en consola). Opcional.
            logger.setUseParentHandlers(true); // true = también en consola

            logger.info("=== Sistema de logs inicializado ===");

        } catch (IOException e) {
            // Si no podemos crear el archivo de log, avisamos a consola.
            // No detenemos la app: solo no habrá logs.
            System.err.println("⚠️ No se pudo inicializar el archivo de logs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================================
    // CONSTRUCTOR PRIVADO
    // ========================================================
    // ¿Por qué?
    // Esta clase es una "utilidad": no queremos que nadie haga
    // "new LogUtil()". Solo se usan sus métodos estáticos.
    // ========================================================
    private LogUtil() {
        // Constructor privado: evita instanciación.
        // Es un patrón común llamado "Utility Class".
    }

    // ========================================================
    // MÉTODOS PÚBLICOS DE LOGGING
    // ========================================================

    /**
     * Registra un mensaje informativo (operación normal).
     * Uso: cuando algo salió bien o es parte del flujo esperado.
     */
    public static void info(String mensaje) {
        logger.info(mensaje);
    }

    /**
     * Registra una advertencia (algo raro pero no crítico).
     * Uso: cuando algo no debería pasar, pero no rompe la app.
     * Ejemplo: intento de login fallido, edad sospechosa, etc.
     */
    public static void warn(String mensaje) {
        logger.warning(mensaje);
    }

    /**
     * Registra un error (algo salió mal).
     * Uso: cuando hay una excepción, un error de BD, etc.
     */
    public static void error(String mensaje) {
        logger.severe(mensaje);
    }

    /**
     * Registra un error con la excepción completa (stack trace).
     * Uso: para ver QUÉ falló exactamente y DÓNDE.
     */
    public static void error(String mensaje, Throwable excepcion) {
        logger.log(Level.SEVERE, mensaje, excepcion);
    }

    /**
     * Registra un mensaje de debug (información fina).
     * Uso: para desarrollo, ver variables, validar flujos.
     * NOTA: con nivel INFO, los debug() NO se guardarán.
     *       Para verlos, cambiar logger.setLevel(Level.FINE).
     */
    public static void debug(String mensaje) {
        logger.fine(mensaje);
    }
}