// ============================================================
// PAQUETE: com.udb.clinica
// CLASE:   PruebaExcepciones
//
// ¿QUÉ HACE?
// Prueba la jerarquía de excepciones personalizadas:
//   - ClinicaException (base)
//   - DuplicadoException (extiende ClinicaException)
//   - NoEncontradoException (extiende ClinicaException)
//
// ¿QUÉ APRENDEMOS?
// - try/catch/throw
// - Polimorfismo aplicado a excepciones
// - Uso del LogUtil para registrar advertencias
// ============================================================

package com.udb.clinica.pruebas;

// Import de TODAS las excepciones propias del sistema
import com.udb.clinica.excepciones.*;

// Import del sistema de logs (lo usaremos para registrar advertencias)
import com.udb.clinica.util.LogUtil;

/**
 * Clase de prueba para verificar que las excepciones personalizadas
 * funcionan correctamente, incluyendo el polimorfismo en catch.
 */
public class PruebaExcepciones {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DE EXCEPCIONES ===\n");

        // ====================================================
        // CASO 1: Capturar DuplicadoException específicamente
        // ====================================================
        // Aquí comprobamos que se puede capturar el tipo exacto.
        // ====================================================
        try {
            simularDuplicado();
        } catch (DuplicadoException e) {
            System.out.println("✅ Capturada DuplicadoException: " + e.getMessage());
            LogUtil.warn("Duplicado detectado: " + e.getMessage());
        } catch (ClinicaException e) {
            // Catch de respaldo (no debería entrar aquí en el caso 1).
            System.out.println("Capturada ClinicaException (base): " + e.getMessage());
        }

        // ====================================================
        // CASO 2: Capturar NoEncontradoException específicamente
        // ====================================================
        try {
            simularNoEncontrado();
        } catch (NoEncontradoException e) {
            System.out.println("✅ Capturada NoEncontradoException: " + e.getMessage());
            LogUtil.warn("No encontrado: " + e.getMessage());
        } catch (ClinicaException e) {
            System.out.println("Capturada ClinicaException (base): " + e.getMessage());
        }

        // ====================================================
        // CASO 3: Capturar con la BASE (Polimorfismo)
        // ====================================================
        // Aquí lanzamos DuplicadoException pero la capturamos con
        // ClinicaException. Funciona porque DuplicadoException
        // ES UNA ClinicaException (herencia).
        // 
        // Esto demuestra el POLIMORFISMO aplicado a excepciones.
        // ====================================================
        System.out.println("\n=== Prueba de polimorfismo en catch ===\n");
        try {
            simularDuplicado();
        } catch (ClinicaException e) {
            System.out.println("✅ Capturada con ClinicaException (base): " + e.getMessage());
            System.out.println("   Tipo real: " + e.getClass().getSimpleName());
        }

        // ====================================================
        // MENSAJE FINAL
        // ====================================================
        System.out.println("\n=== PRUEBA FINALIZADA ===");
        LogUtil.info("Prueba de excepciones finalizada con éxito");
    }

    // ========================================================
    // MÉTODOS AUXILIARES QUE SIMULAN ERRORES
    // ========================================================
    // ¿Por qué están separados en métodos?
    // Porque así imitamos cómo lo hará el DAO más adelante:
    // el método "declara" que puede lanzar la excepción (throws),
    // y quien lo llama debe manejarla (try/catch).
    // ========================================================

    /**
     * Simula el error que lanzaría el DAO cuando el correo ya existe.
     * 
     * @throws DuplicadoException siempre (para la prueba).
     */
    static void simularDuplicado() throws DuplicadoException {
        // throw: lanza la excepción para que quien llama la maneje.
        throw new DuplicadoException("El correo juan@udb.edu.sv ya está registrado");
    }

    /**
     * Simula el error que lanzaría el DAO cuando no encuentra un registro.
     * 
     * @throws NoEncontradoException siempre (para la prueba).
     */
    static void simularNoEncontrado() throws NoEncontradoException {
        throw new NoEncontradoException("Paciente P999 no existe");
    }
}