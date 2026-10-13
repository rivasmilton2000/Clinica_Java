// ============================================================
// PAQUETE: com.udb.clinica.conexion
// CLASE:   ConexionBD
//
// ¿QUÉ HACE?
// Gestiona UNA SOLA conexión a SQL Server (patrón Singleton).
// Proporciona el método getConexion() para que los DAOs la usen.
//
// ¿POR QUÉ SINGLETON?
// Porque abrir/cerrar conexiones es costoso. Con UNA sola
// conexión reutilizable, la app es más rápida y eficiente.
//
// ¿CÓMO SE USA?
//   Connection con = ConexionBD.getConexion();
//   // ... usar con para SELECT/INSERT/UPDATE/DELETE ...
//   // NO cerrar la conexión aquí. Se cierra al final de la app.
//
// CRÉDITOS:
// Basado en el código que el alumno ya tenía funcionando,
// mejorado con Singleton, constantes, LogUtil y manejo correcto
// de excepciones.
// ============================================================

package com.udb.clinica.conexion;

import com.udb.clinica.util.LogUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase Singleton que gestiona la conexión a SQL Server.
 * 
 * <p>Se conecta a la base de datos {@code clinicaUDB} en el servidor
 * configurado en las constantes de esta clase.</p>
 * 
 * <p>Usar el método {@link #getConexion()} para obtener la conexión
 * compartida. NO cerrar la conexión manualmente: se cierra al
 * finalizar la aplicación.</p>
 */
public class ConexionBD {

    // ========================================================
    // CONSTANTES DE CONEXIÓN
    // ========================================================
    // ¿Por qué constantes?
    // - Cambiar la config en un solo lugar.
    // - Legibilidad: se ve claro qué servidor/BD/usuario usamos.
    // - static final = pertenece a la clase, no cambia.
    // ========================================================
    
    /** Nombre del servidor SQL Server (obtenido del entorno del alumno). */
    private static final String SERVIDOR = "localhost";
    
    /** Puerto de SQL Server. Por defecto: 1433. */
    private static final String PUERTO = "1433";
    
    /** Nombre de la base de datos. */
    private static final String BASE_DATOS = "clinicaUDB";
    
    /** Usuario de SQL Server con permisos sobre clinicaUDB. */
    private static final String USUARIO = "clinica";
    
    /** Contraseña del usuario. 
     *  ⚠️ En producción NUNCA se hardcodea. Se usa variable de entorno 
     *  o archivo .properties fuera del repo. Aquí es aceptable por 
     *  ser proyecto académico. */
    private static final String CLAVE = "Clinica123";

    /** Driver JDBC de Microsoft SQL Server. */
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    // ========================================================
    // URL DE CONEXIÓN
    // ========================================================
    // Se construye concatenando las constantes. Los parámetros:
    //   - encrypt=true                → cifra la conexión (obligatorio desde driver v10+).
    //   - trustServerCertificate=true → acepta el certificado del servidor 
    //                                    (útil en desarrollo/local).
    // ========================================================
    private static final String URL =
            "jdbc:sqlserver://" + SERVIDOR + ":" + PUERTO + ";"
            + "databaseName=" + BASE_DATOS + ";"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    // ========================================================
    // INSTANCIA ÚNICA (PATRÓN SINGLETON)
    // ========================================================
    // static → pertenece a la clase, no a un objeto.
    // volatile → asegura que múltiples hilos vean el valor actualizado.
    // ========================================================
    private static volatile Connection conexion = null;

    // ========================================================
    // CONSTRUCTOR PRIVADO
    // ========================================================
    // ¿Por qué privado?
    // Para evitar que alguien haga "new ConexionBD()" y cree 
    // múltiples conexiones. Solo permitimos UNA.
    // ========================================================
    private ConexionBD() {
        // Constructor vacío, privado.
    }

    // ========================================================
    // MÉTODO PRINCIPAL: getConexion()
    // ========================================================
    /**
     * Devuelve la conexión única a la base de datos.
     * Si no existe o está cerrada, la crea.
     * 
     * @return Connection lista para usar.
     * @throws SQLException si no se puede conectar.
     */
    public static Connection getConexion() throws SQLException {

        // --------------------------------------------------
        // Verificar si ya existe una conexión válida
        // --------------------------------------------------
        // conexion == null      → primera vez que se llama.
        // conexion.isClosed()   → la conexión anterior se cerró.
        // --------------------------------------------------
        if (conexion == null || conexion.isClosed()) {

            try {
                // ------------------------------------------
                // Cargar el driver JDBC
                // ------------------------------------------
                // En versiones modernas es opcional, pero lo 
                // mantenemos por compatibilidad y claridad.
                Class.forName(DRIVER);

                // ------------------------------------------
                // Establecer la conexión
                // ------------------------------------------
                conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
                LogUtil.info("✅ Conexión exitosa a " + BASE_DATOS + " en " + SERVIDOR);

            } catch (ClassNotFoundException e) {
                // El driver no está en el classpath.
                LogUtil.error("❌ Driver JDBC no encontrado: " + DRIVER, e);
                throw new SQLException("Driver JDBC no encontrado.", e);

            } catch (SQLException e) {
                // Error de conexión (servidor caído, credenciales malas, etc.).
                LogUtil.error("❌ Error al conectar a la BD: " + e.getMessage(), e);
                throw e;   // Re-lanzamos para que el llamador decida qué hacer.
            }
        }

        return conexion;
    }

    // ========================================================
    // MÉTODO: cerrarConexion()
    // ========================================================
    /**
     * Cierra la conexión a la base de datos.
     * Llamar solo al finalizar la aplicación (o en el shutdown hook).
     */
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                LogUtil.info("Conexión cerrada correctamente.");
                conexion = null;
            } catch (SQLException e) {
                LogUtil.error("Error al cerrar la conexión: " + e.getMessage(), e);
            }
        }
    }
}

//Nestor Osegueda