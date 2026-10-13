// ============================================================
// PAQUETE: com.udb.clinica.excepciones
// CLASE:   DuplicadoException
//
// ¿QUÉ HACE?
// Se lanza cuando se intenta insertar un registro que ya existe
// (según una restricción UNIQUE de la BD).
//
// HERENCIA:
// "extends ClinicaException" → es una excepción de la clínica.
// 
// ¿POR QUÉ EXTIENDE DE ClinicaException Y NO DE Exception?
// Porque agrupa todas las excepciones propias en una jerarquía.
// Así podemos hacer:
//   catch (ClinicaException e)  → atrapa esta y las demás.
//   catch (DuplicadoException e) → atrapa solo esta.
//
// EJEMPLOS DE USO:
//   throw new DuplicadoException("El correo ya está registrado");
//   throw new DuplicadoException("El paciente ya existe", causaOriginal);
// ============================================================

package com.udb.clinica.excepciones;

/**
 * Se lanza cuando un dato ya existe y no se permite duplicar.
 * Ejemplos: correo duplicado, código duplicado, cita en el mismo horario.
 */
public class DuplicadoException extends ClinicaException {

    // ========================================================
    // CONSTRUCTOR 1: solo mensaje
    // ========================================================
    public DuplicadoException(String mensaje) {
        super(mensaje);
    }

    // ========================================================
    // CONSTRUCTOR 2: mensaje + causa
    // ========================================================
    // Ejemplo de uso:
    //   catch (SQLException e) {
    //       if (esErrorDeDuplicado(e)) {
    //           throw new DuplicadoException("Correo duplicado", e);
    //       }
    //   }
    // ========================================================
    public DuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}