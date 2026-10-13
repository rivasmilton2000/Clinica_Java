// ============================================================
// PAQUETE: com.udb.clinica.dao
// CLASE:   CitaDAOImpl
//
// ¿QUÉ HACE?
// Implementa el contrato ICitaDAO con SQL Server.
// 
// ⚠️ PARTICULARIDAD:
// Las horas se envían como STRING ("HH:mm:ss") en lugar de
// java.sql.Time, porque SQL Server no acepta setTime() bien 
// con columnas de tipo TIME.
// 
// ⚠️ OTRA PARTICULARIDAD:
// Los SELECT usan INNER JOIN con 'paciente' y 'doctor' para
// traer los nombres, evitando hacer consultas adicionales.
// ============================================================

package com.udb.clinica.dao;

import com.udb.clinica.conexion.ConexionBD;
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.excepciones.DuplicadoException;
import com.udb.clinica.excepciones.NoEncontradoException;
import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.enums.EstadoCita;
import com.udb.clinica.util.LogUtil;
import com.udb.clinica.util.Validador;

import java.sql.Date;             // java.sql.Date (para JDBC)
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;       // java.time.LocalDate (moderno)
import java.time.LocalTime;       // java.time.LocalTime (moderno)
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link ICitaDAO} con JDBC y SQL Server.
 */
public class CitaDAOImpl implements ICitaDAO {

    // ========================================================
    // FORMATO DE HORA
    // ========================================================
    // Formatea LocalTime como "HH:mm:ss" para SQL Server.
    // Ejemplo: LocalTime.of(15, 30) → "15:30:00"
    // 
    // ¿Por qué?
    // Porque SQL Server no acepta bien java.sql.Time con 
    // columnas TIME. String siempre funciona.
    // ========================================================
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    // ========================================================
    // CONSTANTES SQL
    // ========================================================
    private static final String SQL_INSERT =
            "INSERT INTO cita "
            + "(codigo_cita, codigo_paciente, codigo_doctor, "
            + " fecha_cita, hora_cita, motivo_consulta, estado_cita) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    /** SELECT con JOIN para traer nombres legibles. */
    private static final String SQL_SELECT_BY_CODIGO =
            "SELECT c.*, "
            + "       p.nombre_completo AS nombre_paciente, "
            + "       d.nombre_completo AS nombre_doctor "
            + "FROM cita c "
            + "INNER JOIN paciente p ON c.codigo_paciente = p.codigo_paciente "
            + "INNER JOIN doctor   d ON c.codigo_doctor   = d.codigo_doctor "
            + "WHERE c.codigo_cita = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT c.*, "
            + "       p.nombre_completo AS nombre_paciente, "
            + "       d.nombre_completo AS nombre_doctor "
            + "FROM cita c "
            + "INNER JOIN paciente p ON c.codigo_paciente = p.codigo_paciente "
            + "INNER JOIN doctor   d ON c.codigo_doctor   = d.codigo_doctor "
            + "ORDER BY c.fecha_cita, c.hora_cita";

    private static final String SQL_UPDATE =
            "UPDATE cita SET "
            + "codigo_paciente = ?, "
            + "codigo_doctor = ?, "
            + "fecha_cita = ?, "
            + "hora_cita = ?, "
            + "motivo_consulta = ?, "
            + "estado_cita = ? "
            + "WHERE codigo_cita = ?";

    private static final String SQL_DELETE =
            "DELETE FROM cita WHERE codigo_cita = ?";

    private static final String SQL_SELECT_BY_DOCTOR =
            "SELECT c.*, "
            + "       p.nombre_completo AS nombre_paciente, "
            + "       d.nombre_completo AS nombre_doctor "
            + "FROM cita c "
            + "INNER JOIN paciente p ON c.codigo_paciente = p.codigo_paciente "
            + "INNER JOIN doctor   d ON c.codigo_doctor   = d.codigo_doctor "
            + "WHERE c.codigo_doctor = ? "
            + "ORDER BY c.fecha_cita, c.hora_cita";

    private static final String SQL_SELECT_BY_PACIENTE =
            "SELECT c.*, "
            + "       p.nombre_completo AS nombre_paciente, "
            + "       d.nombre_completo AS nombre_doctor "
            + "FROM cita c "
            + "INNER JOIN paciente p ON c.codigo_paciente = p.codigo_paciente "
            + "INNER JOIN doctor   d ON c.codigo_doctor   = d.codigo_doctor "
            + "WHERE c.codigo_paciente = ? "
            + "ORDER BY c.fecha_cita, c.hora_cita";

    private static final String SQL_SELECT_BY_FECHA =
            "SELECT c.*, "
            + "       p.nombre_completo AS nombre_paciente, "
            + "       d.nombre_completo AS nombre_doctor "
            + "FROM cita c "
            + "INNER JOIN paciente p ON c.codigo_paciente = p.codigo_paciente "
            + "INNER JOIN doctor   d ON c.codigo_doctor   = d.codigo_doctor "
            + "WHERE c.fecha_cita = ? "
            + "ORDER BY c.hora_cita";

    /**
     * REPORTE R1: citas de un doctor en rango de fechas.
     * 
     * BETWEEN es inclusivo (incluye ambos extremos).
     */
    private static final String SQL_SELECT_BY_DOCTOR_RANGO =
            "SELECT c.*, "
            + "       p.nombre_completo AS nombre_paciente, "
            + "       d.nombre_completo AS nombre_doctor "
            + "FROM cita c "
            + "INNER JOIN paciente p ON c.codigo_paciente = p.codigo_paciente "
            + "INNER JOIN doctor   d ON c.codigo_doctor   = d.codigo_doctor "
            + "WHERE c.codigo_doctor = ? "
            + "  AND c.fecha_cita BETWEEN ? AND ? "
            + "ORDER BY c.fecha_cita, c.hora_cita";

    /**
     * REPORTE R3: contar citas agrupadas por estado.
     * GROUP BY agrupa; COUNT(*) cuenta.
     */
    private static final String SQL_CONTAR_POR_ESTADO =
            "SELECT estado_cita, COUNT(*) AS total "
            + "FROM cita "
            + "GROUP BY estado_cita "
            + "ORDER BY estado_cita";

    /**
     * Consulta auxiliar: cuenta cuántas citas activas tiene un doctor
     * en una fecha/hora específica.
     * Se usa para VALIDAR choques de horario antes de insertar.
     */
    private static final String SQL_CONTAR_CHOQUES =
            "SELECT COUNT(*) AS total FROM cita "
            + "WHERE codigo_doctor = ? "
            + "  AND fecha_cita = ? "
            + "  AND hora_cita = ? "
            + "  AND estado_cita <> 'Cancelada'";

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public CitaDAOImpl() { }

    // ========================================================
    // CREATE — INSERTAR
    // ========================================================
    @Override
    public void insertar(Cita c) throws ClinicaException {

        validarCita(c);

        // --------------------------------------------------
        // Verificar choque de horario ANTES de insertar.
        // --------------------------------------------------
        // Aunque la BD tiene un índice único filtrado, queremos
        // dar un mensaje CLARO al usuario antes de que SQL Server
        // lance un error genérico.
        // --------------------------------------------------
        if (hayChoqueDeHorario(c.getCodigoDoctor(), c.getFechaCita(), c.getHoraCita(), null)) {
            throw new DuplicadoException(
                "El doctor ya tiene una cita activa en esa fecha y hora.");
        }

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_INSERT)) {

            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getCodigoPaciente());
            ps.setString(3, c.getCodigoDoctor());
            ps.setDate(  4, Date.valueOf(c.getFechaCita()));
            // ⚠️ Hora como String "HH:mm:ss"
            ps.setString(5, c.getHoraCita().format(FORMATO_HORA));
            ps.setString(6, c.getMotivoConsulta());
            ps.setString(7, c.getEstadoCita().getEtiqueta());

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new ClinicaException("No se insertó la cita (filas: " + filas + ")");
            }

            LogUtil.info("Cita insertada: " + c.getCodigo());

        } catch (SQLException e) {
            manejarSQLException(e, "insertar cita " + c.getCodigo());
        }
    }

    // ========================================================
    // READ — BUSCAR POR CÓDIGO (con JOIN)
    // ========================================================
    @Override
    public Cita buscarPorCodigo(String codigo) throws ClinicaException {

        Validador.requerirNoVacio(codigo, "Código de cita");

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_SELECT_BY_CODIGO)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            } else {
                throw new NoEncontradoException("Cita no encontrada: " + codigo);
            }

        } catch (SQLException e) {
            manejarSQLException(e, "buscar cita " + codigo);
            return null;
        }
    }

    // ========================================================
    // READ — LISTAR TODOS
    // ========================================================
    @Override
    public List<Cita> listarTodos() throws ClinicaException {
        return ejecutarListado(SQL_SELECT_ALL, null, "listar todas las citas");
    }

    // ========================================================
    // READ — LISTAR POR DOCTOR
    // ========================================================
    @Override
    public List<Cita> listarPorDoctor(String codigoDoctor) throws ClinicaException {
        Validador.requerirNoVacio(codigoDoctor, "Código de doctor");
        return ejecutarListado(SQL_SELECT_BY_DOCTOR, codigoDoctor,
                "listar citas del doctor " + codigoDoctor);
    }

    // ========================================================
    // READ — LISTAR POR PACIENTE
    // ========================================================
    @Override
    public List<Cita> listarPorPaciente(String codigoPaciente) throws ClinicaException {
        Validador.requerirNoVacio(codigoPaciente, "Código de paciente");
        return ejecutarListado(SQL_SELECT_BY_PACIENTE, codigoPaciente,
                "listar citas del paciente " + codigoPaciente);
    }

    // ========================================================
    // READ — LISTAR POR FECHA
    // ========================================================
    @Override
    public List<Cita> listarPorFecha(LocalDate fecha) throws ClinicaException {

        if (fecha == null) {
            throw new ClinicaException("La fecha es obligatoria.");
        }

        List<Cita> lista = new ArrayList<>();

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_SELECT_BY_FECHA)) {

            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

            LogUtil.info("Listadas " + lista.size() + " citas para " + fecha);
            return lista;

        } catch (SQLException e) {
            manejarSQLException(e, "listar citas por fecha " + fecha);
            return lista;
        }
    }

    // ========================================================
    // UPDATE — ACTUALIZAR
    // ========================================================
    @Override
    public void actualizar(Cita c) throws ClinicaException {

        validarCita(c);

        // Verificar choque, EXCLUYENDO la propia cita.
        if (hayChoqueDeHorario(c.getCodigoDoctor(), c.getFechaCita(),
                               c.getHoraCita(), c.getCodigo())) {
            throw new DuplicadoException(
                "El doctor ya tiene otra cita activa en esa fecha y hora.");
        }

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_UPDATE)) {

            ps.setString(1, c.getCodigoPaciente());
            ps.setString(2, c.getCodigoDoctor());
            ps.setDate(  3, Date.valueOf(c.getFechaCita()));
            // ⚠️ Hora como String formateado
            ps.setString(4, c.getHoraCita().format(FORMATO_HORA));
            ps.setString(5, c.getMotivoConsulta());
            ps.setString(6, c.getEstadoCita().getEtiqueta());
            ps.setString(7, c.getCodigo());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new NoEncontradoException(
                    "No se puede actualizar: cita no existe " + c.getCodigo());
            }

            LogUtil.info("Cita actualizada: " + c.getCodigo());

        } catch (SQLException e) {
            manejarSQLException(e, "actualizar cita " + c.getCodigo());
        }
    }

    // ========================================================
    // DELETE — ELIMINAR
    // ========================================================
    @Override
    public void eliminar(String codigo) throws ClinicaException {

        Validador.requerirNoVacio(codigo, "Código de cita");

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_DELETE)) {

            ps.setString(1, codigo);
            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new NoEncontradoException(
                    "No se puede eliminar: cita no existe " + codigo);
            }

            LogUtil.info("Cita eliminada: " + codigo);

        } catch (SQLException e) {
            manejarSQLException(e, "eliminar cita " + codigo);
        }
    }

    // ========================================================
    // REPORTES
    // ========================================================
    /**
     * REPORTE R1: citas de un doctor en un rango de fechas.
     * 
     * <p>Usado por PanelReportes para llenar la tabla del reporte
     * "Citas por Doctor".</p>
     */
    @Override
    public List<Cita> listarPorDoctorYRango(String codigoDoctor,
                                            LocalDate desde,
                                            LocalDate hasta) throws ClinicaException {

        Validador.requerirNoVacio(codigoDoctor, "Código de doctor");

        if (desde == null || hasta == null) {
            throw new ClinicaException("Las fechas 'desde' y 'hasta' son obligatorias.");
        }
        if (desde.isAfter(hasta)) {
            throw new ClinicaException("La fecha 'desde' no puede ser mayor que 'hasta'.");
        }

        List<Cita> lista = new ArrayList<>();

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_SELECT_BY_DOCTOR_RANGO)) {

            ps.setString(1, codigoDoctor);
            ps.setDate(  2, Date.valueOf(desde));
            ps.setDate(  3, Date.valueOf(hasta));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

            LogUtil.info("Reporte R1: " + lista.size()
                    + " citas del doctor " + codigoDoctor
                    + " entre " + desde + " y " + hasta);
            return lista;

        } catch (SQLException e) {
            manejarSQLException(e, "listar citas por doctor y rango");
            return lista;
        }
    }

    /**
     * REPORTE R3: contar citas agrupadas por estado.
     * 
     * <p>Usado por PanelReportes para el reporte "Ocupación de la Clínica".</p>
     */
    @Override
    public List<Object[]> contarPorEstado() throws ClinicaException {

        List<Object[]> lista = new ArrayList<>();

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_CONTAR_POR_ESTADO);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String estado = rs.getString("estado_cita");
                int total     = rs.getInt("total");
                lista.add(new Object[]{ estado, total });
            }

            LogUtil.info("Reporte R3: citas por estado generado (" + lista.size() + " filas).");
            return lista;

        } catch (SQLException e) {
            manejarSQLException(e, "contar citas por estado");
            return lista;
        }
    }

    // ========================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ========================================================

    /**
     * Ejecuta un SELECT que devuelve lista de citas con un solo parámetro.
     * Reutilizable por listarTodos, listarPorDoctor, listarPorPaciente.
     */
    private List<Cita> ejecutarListado(String sql, String parametro, String operacion)
            throws ClinicaException {

        List<Cita> lista = new ArrayList<>();

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {

            if (parametro != null) {
                ps.setString(1, parametro);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

            LogUtil.info("Listadas " + lista.size() + " citas (" + operacion + ").");
            return lista;

        } catch (SQLException e) {
            manejarSQLException(e, operacion);
            return lista;
        }
    }

    /**
     * Verifica si el doctor ya tiene una cita activa en la misma fecha/hora.
     * 
     * ⚠️ La hora se compara como STRING en formato "HH:mm:ss".
     * 
     * @param codigoDoctor     doctor a verificar
     * @param fecha            fecha de la cita
     * @param hora             hora de la cita
     * @param codigoExcluir    código de cita a excluir del conteo
     *                         (útil en actualizar, para no contar la propia cita)
     * @return true si hay choque
     */
    private boolean hayChoqueDeHorario(String codigoDoctor, LocalDate fecha,
                                       LocalTime hora, String codigoExcluir)
            throws ClinicaException {

        // Construimos la consulta dinámicamente si hay que excluir una cita.
        String sql = SQL_CONTAR_CHOQUES;
        if (codigoExcluir != null) {
            sql += " AND codigo_cita <> ?";
        }

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {

            ps.setString(1, codigoDoctor);
            ps.setDate(  2, Date.valueOf(fecha));
            // ⚠️ Hora como String
            ps.setString(3, hora.format(FORMATO_HORA));
            if (codigoExcluir != null) {
                ps.setString(4, codigoExcluir);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            return false;

        } catch (SQLException e) {
            manejarSQLException(e, "verificar choque de horario");
            return false;
        }
    }

    /** Valida los campos de una cita. */
    private void validarCita(Cita c) throws ClinicaException {
        Validador.requerirNoVacio(c.getCodigo(), "Código");
        Validador.requerirNoVacio(c.getCodigoPaciente(), "Paciente");
        Validador.requerirNoVacio(c.getCodigoDoctor(), "Doctor");
        Validador.requerirNoVacio(c.getMotivoConsulta(), "Motivo de consulta");

        if (c.getFechaCita() == null) {
            throw new ClinicaException("La fecha de la cita es obligatoria.");
        }
        if (c.getHoraCita() == null) {
            throw new ClinicaException("La hora de la cita es obligatoria.");
        }
        if (c.getEstadoCita() == null) {
            throw new ClinicaException("El estado de la cita es obligatorio.");
        }
    }

    /**
     * Transforma una fila del ResultSet a un objeto Cita.
     * 
     * <p>Incluye la conversión de java.sql.Date/Time a java.time.LocalDate/Time,
     * y el llenado de los campos auxiliares (nombres del JOIN).</p>
     */
    private Cita mapearResultSet(ResultSet rs) throws SQLException {

        String codigo      = rs.getString("codigo_cita");
        String codPaciente = rs.getString("codigo_paciente");
        String codDoctor   = rs.getString("codigo_doctor");
        Date   fechaSql    = rs.getDate("fecha_cita");
        String horaStr     = rs.getString("hora_cita");
        String motivo      = rs.getString("motivo_consulta");
        String estadoStr   = rs.getString("estado_cita");

        // Convertir java.sql.Date → java.time.LocalDate
        LocalDate fecha = fechaSql.toLocalDate();
        // Convertir "HH:mm:ss" → LocalTime
        LocalTime hora  = LocalTime.parse(horaStr);

        EstadoCita estado = EstadoCita.desdeBD(estadoStr);

        Cita c = new Cita(codigo, codPaciente, codDoctor, fecha, hora, motivo, estado);

        // Los JOIN traen los nombres (columnas alias en el SELECT).
        c.setNombrePaciente(rs.getString("nombre_paciente"));
        c.setNombreDoctor(rs.getString("nombre_doctor"));

        return c;
    }

    /**
     * Traduce errores SQL a nuestras excepciones.
     * 
     * @param e el SQLException capturado
     * @param operacion descripción de la operación
     */
    private void manejarSQLException(SQLException e, String operacion)
            throws ClinicaException {

        int codigoError = e.getErrorCode();
        LogUtil.error("Error SQL en " + operacion + " [código " + codigoError + "]: "
                + e.getMessage(), e);

        if (codigoError == 2627 || codigoError == 2601) {
            throw new DuplicadoException("Registro duplicado: " + e.getMessage(), e);
        }
        if (codigoError == 547) {
            throw new ClinicaException(
                "No se puede completar: hay registros relacionados.", e);
        }
        throw new ClinicaException(
            "Error en " + operacion + ": " + e.getMessage(), e);
    }
}

//Nestor Osegueda