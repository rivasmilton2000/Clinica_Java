// ============================================================
// PAQUETE: com.udb.clinica.dao
// TIPO:    Interface
// NOMBRE:  IDoctorDAO
//
// CONTRATO de operaciones CRUD para la entidad Doctor.
// ============================================================

package com.udb.clinica.dao;

import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Doctor;
import java.util.List;

/**
 * Contrato CRUD para la entidad Doctor.
 */
public interface IDoctorDAO {

    /** Inserta un nuevo doctor. */
    void insertar(Doctor doctor) throws ClinicaException;

    /** Busca un doctor por su código. */
    Doctor buscarPorCodigo(String codigo) throws ClinicaException;

    /** Lista todos los doctores. */
    List<Doctor> listarTodos() throws ClinicaException;

    /** Actualiza los datos de un doctor existente. */
    void actualizar(Doctor doctor) throws ClinicaException;

    /** Elimina un doctor por su código. */
    void eliminar(String codigo) throws ClinicaException;
}