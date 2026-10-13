// ============================================================
// PAQUETE: com.udb.clinica.dao
// CLASE:   DoctorDAOImpl
//
// Implementa el contrato IDoctorDAO usando JDBC con SQL Server.
// CRUD completo de la tabla 'doctor'.
// ============================================================

package com.udb.clinica.dao;

import com.udb.clinica.conexion.ConexionBD;
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.excepciones.DuplicadoException;
import com.udb.clinica.excepciones.NoEncontradoException;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.enums.Especialidad;
import com.udb.clinica.modelo.enums.EstadoDoctor;
import com.udb.clinica.util.LogUtil;
import com.udb.clinica.util.Validador;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link IDoctorDAO} con JDBC y SQL Server.
 */
public class DoctorDAOImpl implements IDoctorDAO {

    // ========================================================
    // CONSTANTES SQL
    // ========================================================
    private static final String SQL_INSERT =
            "INSERT INTO doctor "
            + "(codigo_doctor, nombre_completo, especialidad, telefono, correo, estado_doctor) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_CODIGO =
            "SELECT * FROM doctor WHERE codigo_doctor = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM doctor ORDER BY codigo_doctor";

    private static final String SQL_UPDATE =
            "UPDATE doctor SET "
            + "nombre_completo = ?, "
            + "especialidad = ?, "
            + "telefono = ?, "
            + "correo = ?, "
            + "estado_doctor = ? "
            + "WHERE codigo_doctor = ?";

    private static final String SQL_DELETE =
            "DELETE FROM doctor WHERE codigo_doctor = ?";

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public DoctorDAOImpl() { }

    // ========================================================
    // CREATE — INSERTAR
    // ========================================================
    @Override
    public void insertar(Doctor d) throws ClinicaException {

        validarDoctor(d);

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_INSERT)) {

            ps.setString(1, d.getCodigo());
            ps.setString(2, d.getNombreCompleto());
            ps.setString(3, d.getEspecialidad().getEtiqueta());
            ps.setString(4, d.getTelefono());
            ps.setString(5, d.getCorreo());
            ps.setString(6, d.getEstadoDoctor().getEtiqueta());

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new ClinicaException(
                    "No se insertó el doctor (filas: " + filas + ")");
            }

            LogUtil.info("Doctor insertado: " + d.getCodigo() + " - " + d.getNombreCompleto());

        } catch (SQLException e) {
            manejarSQLException(e, "insertar doctor " + d.getCodigo());
        }
    }

    // ========================================================
    // READ — BUSCAR POR CÓDIGO
    // ========================================================
    @Override
    public Doctor buscarPorCodigo(String codigo) throws ClinicaException {

        Validador.requerirNoVacio(codigo, "Código de doctor");

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_SELECT_BY_CODIGO)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            } else {
                throw new NoEncontradoException("Doctor no encontrado: " + codigo);
            }

        } catch (SQLException e) {
            manejarSQLException(e, "buscar doctor " + codigo);
            return null;
        }
    }

    // ========================================================
    // READ — LISTAR TODOS
    // ========================================================
    @Override
    public List<Doctor> listarTodos() throws ClinicaException {

        List<Doctor> lista = new ArrayList<>();

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

            LogUtil.info("Listados " + lista.size() + " doctores.");
            return lista;

        } catch (SQLException e) {
            manejarSQLException(e, "listar doctores");
            return lista;
        }
    }

    // ========================================================
    // UPDATE — ACTUALIZAR
    // ========================================================
    @Override
    public void actualizar(Doctor d) throws ClinicaException {

        validarDoctor(d);

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_UPDATE)) {

            ps.setString(1, d.getNombreCompleto());
            ps.setString(2, d.getEspecialidad().getEtiqueta());
            ps.setString(3, d.getTelefono());
            ps.setString(4, d.getCorreo());
            ps.setString(5, d.getEstadoDoctor().getEtiqueta());
            ps.setString(6, d.getCodigo());   // WHERE

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new NoEncontradoException(
                    "No se puede actualizar: doctor no existe " + d.getCodigo());
            }

            LogUtil.info("Doctor actualizado: " + d.getCodigo());

        } catch (SQLException e) {
            manejarSQLException(e, "actualizar doctor " + d.getCodigo());
        }
    }

    // ========================================================
    // DELETE — ELIMINAR
    // ========================================================
    @Override
    public void eliminar(String codigo) throws ClinicaException {

        Validador.requerirNoVacio(codigo, "Código de doctor");

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(SQL_DELETE)) {

            ps.setString(1, codigo);
            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new NoEncontradoException(
                    "No se puede eliminar: doctor no existe " + codigo);
            }

            LogUtil.info("Doctor eliminado: " + codigo);

        } catch (SQLException e) {
            manejarSQLException(e, "eliminar doctor " + codigo);
        }
    }

    // ========================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // ========================================================

    /** Valida los campos de un doctor. */
    private void validarDoctor(Doctor d) throws ClinicaException {
        Validador.requerirNoVacio(d.getCodigo(), "Código");
        Validador.requerirNoVacio(d.getNombreCompleto(), "Nombre completo");
        Validador.requerirNoVacio(d.getTelefono(), "Teléfono");
        Validador.requerirNoVacio(d.getCorreo(), "Correo");
        Validador.requerirTelefonoValido(d.getTelefono());
        Validador.requerirCorreoValido(d.getCorreo());

        if (d.getEspecialidad() == null) {
            throw new ClinicaException("La especialidad es obligatoria.");
        }
        if (d.getEstadoDoctor() == null) {
            throw new ClinicaException("El estado del doctor es obligatorio.");
        }
    }

    /** Transforma una fila del ResultSet a un objeto Doctor. */
    private Doctor mapearResultSet(ResultSet rs) throws SQLException {

        String codigo      = rs.getString("codigo_doctor");
        String nombre      = rs.getString("nombre_completo");
        String espStr      = rs.getString("especialidad");
        String telefono    = rs.getString("telefono");
        String correo      = rs.getString("correo");
        String estadoStr   = rs.getString("estado_doctor");

        Especialidad esp       = Especialidad.desdeBD(espStr);
        EstadoDoctor estado    = EstadoDoctor.desdeBD(estadoStr);

        return new Doctor(codigo, nombre, telefono, correo, esp, estado);
    }

    /** Traduce errores SQL a nuestras excepciones. */
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