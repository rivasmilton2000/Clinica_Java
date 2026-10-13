// ============================================================
// PAQUETE: com.udb.clinica.excepciones
// CLASE:   NoEncontradoException
//
// ¿QUÉ HACE?
// Se lanza cuando se busca/actualiza/elimina un registro que
// NO existe en la BD.
//
// HERENCIA:
// "extends ClinicaException" → es una excepción de la clínica.
//
// EJEMPLOS DE USO:
//   throw new NoEncontradoException("Paciente P999 no existe");
//   throw new NoEncontradoException("Cita C001 no encontrada");
// ============================================================

package com.udb.clinica.excepciones;

/**
 * Se lanza cuando un registro no existe en la base de datos.
 */
public class NoEncontradoException extends ClinicaException {

    // ========================================================
    // CONSTRUCTOR 1: solo mensaje
    // ========================================================
    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }

    // ========================================================
    // CONSTRUCTOR 2: mensaje + causa
    // ========================================================
    public NoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}