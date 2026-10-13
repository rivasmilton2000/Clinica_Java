// ============================================================
// PAQUETE: com.udb.clinica.dao
// TIPO:    Interface
// NOMBRE:  ICitaDAO
//
// ¿QUÉ ES?
// El CONTRATO de operaciones CRUD para la entidad Cita.
// 
// ¿POR QUÉ TIENE MÁS MÉTODOS QUE LOS OTROS DAOs?
// La entidad Cita es la más rica en consultas:
// - CRUD básico (5 métodos).
// - Consultas específicas: por doctor, por paciente, por fecha.
// - Reportes: por rango de fechas, conteo por estado.
// 
// ¿QUIÉN LA IMPLEMENTA?
//   CitaDAOImpl (con JDBC para SQL Server).
// ============================================================

package com.udb.clinica.dao;

import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Cita;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrato CRUD para la entidad Cita.
 */
public interface ICitaDAO {

    // ========================================================
    // CRUD básico
    // ========================================================
    /**
     * Inserta una nueva cita.
     * Valida que el doctor no tenga otra cita activa en la misma fecha/hora.
     */
    void insertar(Cita cita) throws ClinicaException;

    /**
     * Busca una cita por su código.
     * Incluye JOIN con paciente y doctor para traer los nombres.
     */
    Cita buscarPorCodigo(String codigo) throws ClinicaException;

    /**
     * Lista todas las citas.
     * Ordenadas por fecha y hora ascendente.
     */
    List<Cita> listarTodos() throws ClinicaException;

    /**
     * Actualiza una cita existente.
     * Vuelve a validar el choque de horario (excluyendo la propia cita).
     */
    void actualizar(Cita cita) throws ClinicaException;

    /**
     * Elimina una cita por su código.
     */
    void eliminar(String codigo) throws ClinicaException;

    // ========================================================
    // Consultas ESPECÍFICAS de Cita
    // ========================================================

    /**
     * Lista las citas de un doctor específico.
     * @param codigoDoctor código del doctor.
     */
    List<Cita> listarPorDoctor(String codigoDoctor) throws ClinicaException;

    /**
     * Lista las citas de un paciente específico.
     * @param codigoPaciente código del paciente.
     */
    List<Cita> listarPorPaciente(String codigoPaciente) throws ClinicaException;

    /**
     * Lista las citas de una fecha específica.
     * @param fecha la fecha a consultar.
     */
    List<Cita> listarPorFecha(LocalDate fecha) throws ClinicaException;

    // ========================================================
    // REPORTES
    // ========================================================

    /**
     * REPORTE R1: lista citas de un doctor en un rango de fechas (inclusive).
     * 
     * <p>Usado por PanelReportes para el reporte
     * "Citas por Doctor en un rango de fechas".</p>
     * 
     * @param codigoDoctor Código del doctor a filtrar.
     * @param desde        Fecha inicial del rango.
     * @param hasta        Fecha final del rango.
     * @return Lista de citas filtradas, ordenadas por fecha y hora.
     */
    List<Cita> listarPorDoctorYRango(String codigoDoctor,
                                     LocalDate desde,
                                     LocalDate hasta) throws ClinicaException;

    /**
     * REPORTE R3: cuenta las citas agrupadas por estado.
     * 
     * <p>Usado por PanelReportes para el reporte "Ocupación de la Clínica".</p>
     * 
     * <p>Ejemplo de retorno:</p>
     * <pre>
     *   [ ["Programada", 3], ["Atendida", 1], ["Cancelada", 0] ]
     * </pre>
     * 
     * @return Lista de arrays [String estado, Integer cantidad].
     */
    List<Object[]> contarPorEstado() throws ClinicaException;
}