// ============================================================
// PAQUETE: com.udb.clinica.excepciones
// CLASE:   ClinicaException
//
// ¿QUÉ HACE?
// Es la excepción BASE de todo el sistema Clínica UDB.
// Todas las excepciones específicas (DuplicadoException,
// NoEncontradoException) heredan de esta.
//
// ¿POR QUÉ EXISTE?
// Para poder capturar CUALQUIER error del sistema con un solo catch:
//   catch (ClinicaException e) { ... }  // atrapa las 3 de golpe
//
// ¿POR QUÉ "extends Exception" y no "extends RuntimeException"?
// - Exception          → excepción COMPROBADA (checked). Obliga a
//                        manejar con try/catch o declarar throws.
// - RuntimeException   → excepción NO comprobada (unchecked).
//
// Preferimos la versión comprobada: obliga al programador a
// pensar en el manejo de errores.
// ============================================================

package com.udb.clinica.excepciones;

/**
 * Excepción base del sistema Clínica UDB.
 * 
 * Cualquier error propio del sistema debe extender de esta clase.
 */
public class ClinicaException extends Exception {

    // ========================================================
    // CONSTRUCTOR 1: solo mensaje
    // ========================================================
    // Uso: throw new ClinicaException("Algo salió mal");
    // ========================================================
    public ClinicaException(String mensaje) {
        super(mensaje);  // super = llama al constructor de Exception
    }

    // ========================================================
    // CONSTRUCTOR 2: mensaje + causa
    // ========================================================
    // Uso: cuando queremos "encapsular" otra excepción dentro de la nuestra.
    // Ejemplo:
    //   try {
    //       conectar();
    //   } catch (SQLException e) {
    //       throw new ClinicaException("Error de BD", e);
    //   }
    // 
    // Así preservamos el stack trace original para depurar.
    // ========================================================
    public ClinicaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}