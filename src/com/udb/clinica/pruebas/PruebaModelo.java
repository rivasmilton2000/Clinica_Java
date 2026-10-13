package com.udb.clinica.pruebas;

import com.udb.clinica.modelo.*;
import com.udb.clinica.modelo.enums.*;
import com.udb.clinica.util.LogUtil;     // ← import del LogUtil
import java.time.LocalDate;
import java.time.LocalTime;

public class PruebaModelo {
    public static void main(String[] args) {

        // ====================================================
        // REGISTRAR INICIO EN EL LOG
        // ====================================================
        LogUtil.info("Iniciando prueba del modelo");

        System.out.println("=== PRUEBA 1: HERENCIA Y POLIMORFISMO ===\n");

        Paciente p = new Paciente("P001", "Juan Pérez", "7777-1234",
                "juan@udb.edu.sv", 20, TipoPaciente.ESTUDIANTE,
                EstadoPaciente.ACTIVO, GravedadPaciente.LEVE);
        System.out.println(p);

        Doctor d = new Doctor("D001", "Dr. Sánchez", "2222-1111",
                "sanchez@udb.edu.sv", Especialidad.MEDICINA_GENERAL,
                EstadoDoctor.ACTIVO);
        System.out.println(d);

        System.out.println("\n=== PRUEBA 2: POLIMORFISMO ===\n");
        Persona[] personas = { p, d };
        for (Persona persona : personas) {
            System.out.println("Rol: " + persona.getRol() + " → " + persona.getNombreCompleto());
        }

        System.out.println("\n=== PRUEBA 3: ASOCIACIÓN (Cita) ===\n");
        Cita c = new Cita("C001", "P001", "D001",
                LocalDate.of(2026, 9, 25), LocalTime.of(9, 0),
                "Dolor de cabeza", EstadoCita.PROGRAMADA);
        System.out.println(c);

        System.out.println("\n=== PRUEBA 4: toString() POLIMÓRFICO ===\n");
        for (Persona persona : personas) {
            System.out.println(persona.toString());
        }

        // ====================================================
        // REGISTRAR FIN EN EL LOG
        // ====================================================
        LogUtil.info("Prueba del modelo finalizada con éxito");
    }
}