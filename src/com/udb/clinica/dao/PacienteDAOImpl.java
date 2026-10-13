// ============================================================
// PAQUETE: com.udb.clinica.dao
// CLASE:   PacienteDAOImpl
//
// ¿QUÉ HACE?
// Implementa el contrato IPacienteDAO con SQL Server.
// Realiza el CRUD completo de la tabla 'paciente':
//   - INSERT
//   - SELECT (uno y todos)
//   - UPDATE
//   - DELETE
// Y el reporte R2 (contar por tipo).
//
// ¿POR QUÉ EXISTE?
// - Encapsula todo el SQL en un solo lugar.
// - La vista y otros componentes no ven SQL, solo el contrato.
// - Si mañana cambias de BD, creas otra implementación.
//
// ¿CÓMO SE USA?
//   IPacienteDAO dao = new PacienteDAOImpl();
//   dao.insertar(new Paciente(...));
// ============================================================

package com.udb.clinica.dao;

// Import de nuestras excepciones personalizadas
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.excepciones.DuplicadoException;
import com.udb.clinica.excepciones.NoEncontradoException;

// Import de la conexión Singleton
import com.udb.clinica.conexion.ConexionBD;

// Import del modelo (Paciente) y sus enums
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.enums.EstadoPaciente;
import com.udb.clinica.modelo.enums.GravedadPaciente;
import com.udb.clinica.modelo.enums.TipoPaciente;

// Import de utilidades
import com.udb.clinica.util.LogUtil;
import com.udb.clinica.util.Validador;

// Imports de JDBC
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del contrato {@link IPacienteDAO} usando JDBC con SQL Server.
 * 
 * <p>Todas las operaciones registran logs y transforman las excepciones
 * de SQL en nuestras excepciones personalizadas.</p>
 */
public class PacienteDAOImpl implements IPacienteDAO {

    // ========================================================
    // CONSTANTES DE SQL
    // ========================================================
    // Guardamos las consultas SQL como constantes.
    // 
    // ¿Por qué?
    // - Evita errores de tipeo al repetir el SQL.
    // - Se ven todas las consultas juntas al inicio de la clase.
    // - Fácil de modificar si cambia el esquema.
    // ========================================================

    /** INSERT de un nuevo paciente. 8 columnas = 8 placeholders. */
    private static final String SQL_INSERT =
            "INSERT INTO paciente "
            + "(codigo_paciente, nombre_completo, edad, telefono, correo, "
            + " tipo_paciente, estado_paciente, gravedad_paciente) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /** SELECT de un paciente por código (devuelve máximo 1 fila). */
    private static final String SQL_SELECT_BY_CODIGO =
            "SELECT * FROM paciente WHERE codigo_paciente = ?";

    /** SELECT de todos los pacientes, ordenados por código. */
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM paciente ORDER BY codigo_paciente";

    /** UPDATE de los datos de un paciente (excepto el código, que es la PK). */
    private static final String SQL_UPDATE =
            "UPDATE paciente SET "
            + "nombre_completo = ?, "
            + "edad = ?, "
            + "telefono = ?, "
            + "correo = ?, "
            + "tipo_paciente = ?, "
            + "estado_paciente = ?, "
            + "gravedad_paciente = ? "
            + "WHERE codigo_paciente = ?";

    /** DELETE de un paciente por código. */
    private static final String SQL_DELETE =
            "DELETE FROM paciente WHERE codigo_paciente = ?";

    /**
     * REPORTE R2: contar pacientes agrupados por tipo.
     * 
     * GROUP BY agrupa filas por valor de 'tipo_paciente' y
     * COUNT(*) cuenta cuántas hay en cada grupo.
     */
    private static final String SQL_CONTAR_POR_TIPO =
            "SELECT tipo_paciente, COUNT(*) AS total "
            + "FROM paciente "
            + "GROUP BY tipo_paciente "
            + "ORDER BY tipo_paciente";

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    // No hay atributos en esta clase. Los DAOs son "stateless"
    // (sin estado): cada método usa la conexión Singleton.
    // ========================================================
    public PacienteDAOImpl() {
        // Constructor vacío.
    }

    // ========================================================
    // CREATE — INSERTAR
    // ========================================================
    @Override
    public void insertar(Paciente p) throws ClinicaException {

        // --------------------------------------------------
        // PASO 1: VALIDAR los datos antes de tocar la BD.
        // --------------------------------------------------
        // ¿Por qué validar primero?
        // - Ahorramos una ida y vuelta a la BD si el dato es malo.
        // - Los mensajes de error son más específicos.
        // --------------------------------------------------
        validarPaciente(p);

        // --------------------------------------------------
        // PASO 2: Ejecutar INSERT con PreparedStatement.
        // --------------------------------------------------
        // NO cerramos la Connection (es Singleton).
        // El PreparedStatement SÍ se cierra automáticamente 
        // con try-with-resources.
        // --------------------------------------------------
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_INSERT)) {

            // Asignar cada atributo del objeto al placeholder (?).
            // ⚠️ Los índices empiezan en 1 (no en 0).
            ps.setString(1, p.getCodigo());                       // codigo_paciente
            ps.setString(2, p.getNombreCompleto());               // nombre_completo
            ps.setInt(   3, p.getEdad());                         // edad
            ps.setString(4, p.getTelefono());                     // telefono
            ps.setString(5, p.getCorreo());                       // correo
            ps.setString(6, p.getTipoPaciente().getEtiqueta());   // tipo_paciente
            ps.setString(7, p.getEstadoPaciente().getEtiqueta()); // estado_paciente
            ps.setString(8, p.getGravedad().getEtiqueta());       // gravedad_paciente

            // executeUpdate() devuelve la cantidad de filas afectadas.
            int filas = ps.executeUpdate();

            // Verificación defensiva: debería ser 1.
            if (filas != 1) {
                throw new ClinicaException(
                    "No se insertó el paciente (filas: " + filas + ")");
            }

            LogUtil.info("Paciente insertado: " + p.getCodigo() + " - " + p.getNombreCompleto());

        } catch (SQLException e) {
            // Transformar la excepción SQL en algo más útil.
            manejarSQLException(e, "insertar paciente " + p.getCodigo());
        }
    }

    // ========================================================
    // READ — BUSCAR POR CÓDIGO
    // ========================================================
    @Override
    public Paciente buscarPorCodigo(String codigo) throws ClinicaException {

        // Validar entrada
        Validador.requerirNoVacio(codigo, "Código de paciente");

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_SELECT_BY_CODIGO)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            // Si hay una fila, mapearla a Paciente y devolverla.
            if (rs.next()) {
                return mapearResultSet(rs);
            } else {
                // No hay fila con ese código → no existe.
                throw new NoEncontradoException("Paciente no encontrado: " + codigo);
            }

        } catch (SQLException e) {
            manejarSQLException(e, "buscar paciente " + codigo);
            return null;   // Nunca se alcanza, pero el compilador lo exige.
        }
    }

    // ========================================================
    // READ — LISTAR TODOS
    // ========================================================
    @Override
    public List<Paciente> listarTodos() throws ClinicaException {

        // Creamos una lista vacía donde iremos agregando pacientes.
        List<Paciente> lista = new ArrayList<>();

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            // rs.next() se mueve fila por fila. Devuelve false al final.
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

            LogUtil.info("Listados " + lista.size() + " pacientes.");
            return lista;

        } catch (SQLException e) {
            manejarSQLException(e, "listar pacientes");
            return lista;   // Nunca se alcanza.
        }
    }

    // ========================================================
    // UPDATE — ACTUALIZAR
    // ========================================================
    @Override
    public void actualizar(Paciente p) throws ClinicaException {

        validarPaciente(p);

        // --------------------------------------------------
        // ¿Por qué verificar existencia primero?
        // --------------------------------------------------
        // UPDATE de una fila inexistente afecta 0 filas,
        // pero NO da error. Si no verificamos, el usuario
        // cree que actualizó algo que en realidad no existía.
        // --------------------------------------------------
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_UPDATE)) {

            ps.setString(1, p.getNombreCompleto());
            ps.setInt(   2, p.getEdad());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getTipoPaciente().getEtiqueta());
            ps.setString(6, p.getEstadoPaciente().getEtiqueta());
            ps.setString(7, p.getGravedad().getEtiqueta());
            ps.setString(8, p.getCodigo());   // ← WHERE codigo_paciente = ?

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new NoEncontradoException(
                    "No se puede actualizar: paciente no existe " + p.getCodigo());
            }

            LogUtil.info("Paciente actualizado: " + p.getCodigo());

        } catch (SQLException e) {
            manejarSQLException(e, "actualizar paciente " + p.getCodigo());
        }
    }

    // ========================================================
    // DELETE — ELIMINAR
    // ========================================================
    @Override
    public void eliminar(String codigo) throws ClinicaException {

        Validador.requerirNoVacio(codigo, "Código de paciente");

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_DELETE)) {

            ps.setString(1, codigo);
            int filas = ps.executeUpdate();

            // Si no afectó filas, el paciente no existía.
            if (filas == 0) {
                throw new NoEncontradoException(
                    "No se puede eliminar: paciente no existe " + codigo);
            }

            LogUtil.info("Paciente eliminado: " + codigo);

        } catch (SQLException e) {
            // Si el paciente tiene citas (FK), SQL Server devolverá error 547.
            manejarSQLException(e, "eliminar paciente " + codigo);
        }
    }

    // ========================================================
    // REPORTES
    // ========================================================
    /**
     * REPORTE R2: cuenta pacientes por tipo.
     * 
     * <p>Se usa desde PanelReportes para llenar la tabla del reporte
     * "Pacientes por tipo".</p>
     * 
     * <p>El SQL hace un GROUP BY sobre la columna 'tipo_paciente' y
     * devuelve dos columnas: el tipo y el conteo.</p>
     */
    @Override
    public List<Object[]> contarPorTipo() throws ClinicaException {

        List<Object[]> lista = new ArrayList<>();

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_CONTAR_POR_TIPO);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("tipo_paciente");
                int total   = rs.getInt("total");

                // Guardamos en un array de Object porque los tipos son
                // heterogéneos: String + int.
                lista.add(new Object[]{ tipo, total });
            }

            LogUtil.info("Reporte R2: pacientes por tipo generado (" + lista.size() + " filas).");
            return lista;

        } catch (SQLException e) {
            manejarSQLException(e, "contar pacientes por tipo");
            return lista;
        }
    }

    // ========================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ========================================================

    /**
     * Valida todos los campos de un paciente antes de insertar/actualizar.
     * Lanza excepción si algo está mal.
     */
    private void validarPaciente(Paciente p) throws ClinicaException {
        Validador.requerirNoVacio(p.getCodigo(), "Código");
        Validador.requerirNoVacio(p.getNombreCompleto(), "Nombre completo");
        Validador.requerirNoVacio(p.getTelefono(), "Teléfono");
        Validador.requerirNoVacio(p.getCorreo(), "Correo");
        Validador.requerirEdadValida(p.getEdad());
        Validador.requerirTelefonoValido(p.getTelefono());
        Validador.requerirCorreoValido(p.getCorreo());

        if (p.getTipoPaciente() == null) {
            throw new ClinicaException("El tipo de paciente es obligatorio.");
        }
        if (p.getEstadoPaciente() == null) {
            throw new ClinicaException("El estado del paciente es obligatorio.");
        }
        if (p.getGravedad() == null) {
            throw new ClinicaException("La gravedad del paciente es obligatoria.");
        }
    }

    /**
     * Transforma una fila del ResultSet en un objeto Paciente.
     * 
     * <p>Se usa en buscarPorCodigo() y listarTodos().</p>
     */
    private Paciente mapearResultSet(ResultSet rs) throws SQLException {

        // Leer cada columna por nombre (más seguro que por índice).
        String codigo      = rs.getString("codigo_paciente");
        String nombre      = rs.getString("nombre_completo");
        int    edad        = rs.getInt("edad");
        String telefono    = rs.getString("telefono");
        String correo      = rs.getString("correo");
        String tipoStr     = rs.getString("tipo_paciente");
        String estadoStr   = rs.getString("estado_paciente");
        String gravedadStr = rs.getString("gravedad_paciente");

        // Convertir Strings de BD a enums con el método desdeBD().
        TipoPaciente tipo         = TipoPaciente.desdeBD(tipoStr);
        EstadoPaciente estado     = EstadoPaciente.desdeBD(estadoStr);
        GravedadPaciente gravedad = GravedadPaciente.desdeBD(gravedadStr);

        // Construir el objeto Paciente.
        return new Paciente(codigo, nombre, telefono, correo,
                            edad, tipo, estado, gravedad);
    }

    /**
     * Maneja las excepciones SQL de manera centralizada:
     * - Detecta duplicados (2627, 2601) y lanza DuplicadoException.
     * - Detecta FK violation (547) y lanza ClinicaException con mensaje claro.
     * - Cualquier otro error → ClinicaException genérica.
     * 
     * @param e el SQLException capturado
     * @param operacion descripción de la operación ("insertar paciente P005")
     */
    private void manejarSQLException(SQLException e, String operacion)
            throws ClinicaException {

        int codigoError = e.getErrorCode();

        // Log del error original.
        LogUtil.error("Error SQL en " + operacion + " [código " + codigoError + "]: "
                + e.getMessage(), e);

        // ----------------------------------------------
        // Detectar duplicados (violación de UNIQUE)
        // ----------------------------------------------
        // 2627: violación de constraint UNIQUE/PK
        // 2601: intento de insertar clave duplicada en índice único
        if (codigoError == 2627 || codigoError == 2601) {
            throw new DuplicadoException(
                "Registro duplicado: " + e.getMessage(), e);
        }

        // ----------------------------------------------
        // Detectar violación de Foreign Key (error 547)
        // ----------------------------------------------
        // Ejemplo: intentar borrar un paciente con citas asociadas.
        if (codigoError == 547) {
            throw new ClinicaException(
                "No se puede completar la operación: hay registros relacionados.", e);
        }

        // ----------------------------------------------
        // Cualquier otro error SQL → ClinicaException genérica.
        // ----------------------------------------------
        throw new ClinicaException(
            "Error en " + operacion + ": " + e.getMessage(), e);
    }
}

//Nestor Osegueda