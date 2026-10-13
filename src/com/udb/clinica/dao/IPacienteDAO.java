// ============================================================
// PAQUETE: com.udb.clinica.dao
// TIPO:    Interface
// NOMBRE:  IPacienteDAO
//
// ¿QUÉ ES?
// El CONTRATO de operaciones CRUD para la entidad Paciente.
// 
// ¿POR QUÉ EXISTE?
// - Define QUÉ se puede hacer, sin decir CÓMO.
// - Permite cambiar la implementación (SQL Server, MySQL, Oracle)
//   sin tocar el resto del sistema.
// - La vista solo conoce esta interface, no la implementación.
// 
// ¿QUIÉN LA IMPLEMENTA?
//   PacienteDAOImpl (con JDBC para SQL Server)
//
// MÉTODOS:
//   - CRUD básico: insertar, buscarPorCodigo, listarTodos, actualizar, eliminar
//   - Reporte:     contarPorTipo (agrupación por tipo de paciente)
// ============================================================

package com.udb.clinica.dao;

import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Paciente;
import java.util.List;

/**
 * Contrato de operaciones CRUD para la entidad Paciente.
 * 
 * <p>Todas las operaciones lanzan {@link ClinicaException} si algo falla.</p>
 */
public interface IPacienteDAO {

    // ========================================================
    // CREATE
    // ========================================================
    /**
     * Inserta un nuevo paciente en la base de datos.
     * 
     * @param paciente el paciente a insertar
     * @throws ClinicaException si hay error de validación,
     *                          duplicado, o problema de BD
     */
    void insertar(Paciente paciente) throws ClinicaException;

    // ========================================================
    // READ
    // ========================================================
    /**
     * Busca un paciente por su código único.
     * 
     * @param codigo el código del paciente (ej. "P001")
     * @return el Paciente encontrado
     * @throws ClinicaException si no se encuentra o hay error
     */
    Paciente buscarPorCodigo(String codigo) throws ClinicaException;

    /**
     * Devuelve todos los pacientes registrados.
     * 
     * @return lista de Pacientes (puede estar vacía)
     * @throws ClinicaException si hay error de BD
     */
    List<Paciente> listarTodos() throws ClinicaException;

    // ========================================================
    // UPDATE
    // ========================================================
    /**
     * Actualiza los datos de un paciente existente.
     * El código se usa para localizar el registro.
     * 
     * @param paciente el paciente con los datos actualizados
     * @throws ClinicaException si no existe o hay error
     */
    void actualizar(Paciente paciente) throws ClinicaException;

    // ========================================================
    // DELETE
    // ========================================================
    /**
     * Elimina un paciente por su código.
     * 
     * @param codigo el código del paciente a eliminar
     * @throws ClinicaException si no existe o hay error
     */
    void eliminar(String codigo) throws ClinicaException;

    // ========================================================
    // REPORTES
    // ========================================================
    /**
     * Cuenta los pacientes agrupados por tipo.
     * 
     * <p>Usa un GROUP BY en SQL Server para devolver, por cada tipo
     * de paciente (Estudiante, Docente, Administrativo, Visitante),
     * la cantidad de registros que existen.</p>
     * 
     * <p>Ejemplo de retorno:</p>
     * <pre>
     *   [ ["Estudiante", 5], ["Docente", 3], ["Visitante", 2] ]
     * </pre>
     * 
     * @return Lista de arrays [String tipo, Integer cantidad].
     * @throws ClinicaException si hay error de BD.
     */
    List<Object[]> contarPorTipo() throws ClinicaException;
}