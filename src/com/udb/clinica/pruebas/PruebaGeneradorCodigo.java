package com.udb.clinica.pruebas;

import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.util.GeneradorCodigo;
import com.udb.clinica.util.LogUtil;

public class PruebaGeneradorCodigo {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE GENERADOR DE CÓDIGOS ===\n");

        try {
            // En la BD ya tienes: P001, P002, P003, P004
            String sigPaciente = GeneradorCodigo.siguienteCodigoPaciente();
            System.out.println("Próximo paciente: " + sigPaciente + "  (esperado: P005)");

            // En la BD ya tienes: D001, D002, D003, D004
            String sigDoctor = GeneradorCodigo.siguienteCodigoDoctor();
            System.out.println("Próximo doctor:   " + sigDoctor + "  (esperado: D005)");

            // En la BD ya tienes: C001, C002, C003, C004
            String sigCita = GeneradorCodigo.siguienteCodigoCita();
            System.out.println("Próxima cita:     " + sigCita + "  (esperado: C005)");

            // Probar varias veces para ver la secuencia
            System.out.println("\n--- Simulando varios códigos seguidos (sin insertar) ---");
            System.out.println("Si insertáramos, los próximos códigos de paciente serían:");
            for (int i = 0; i < 5; i++) {
                System.out.println("  " + GeneradorCodigo.siguienteCodigoPaciente()
                        + "  (recuerda: solo avanza si insertas)");
            }

            LogUtil.info("Prueba de GeneradorCodigo finalizada.");

        } catch (ClinicaException e) {
            System.err.println("❌ Error: " + e.getMessage());
            LogUtil.error("Error en prueba de GeneradorCodigo", e);
        }
    }
}