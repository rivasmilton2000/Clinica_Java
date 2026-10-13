package com.udb.clinica.pruebas;

import com.udb.clinica.dao.*;
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.enums.*;
import com.udb.clinica.util.GeneradorCodigo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PruebaDoctorCitaDAO {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DOCTOR Y CITA DAO ===\n");

        IDoctorDAO doctorDAO = new DoctorDAOImpl();
        ICitaDAO   citaDAO   = new CitaDAOImpl();

        try {
            // ---------- DOCTORES ----------
            System.out.println("--- DOCTORES ---");
            List<Doctor> doctores = doctorDAO.listarTodos();
            doctores.forEach(d -> System.out.println("  " + d));

            // ---------- CITAS ----------
            System.out.println("\n--- CITAS (con JOIN) ---");
            List<Cita> citas = citaDAO.listarTodos();
            citas.forEach(c -> System.out.println("  " + c.getCodigo()
                    + " | Pac: " + c.getNombrePaciente()
                    + " | Doc: " + c.getNombreDoctor()
                    + " | " + c.getFechaCita() + " " + c.getHoraCita()));

            // ---------- INSERTAR CITA ----------
            System.out.println("\n--- INSERTAR CITA NUEVA ---");
            String cod = GeneradorCodigo.siguienteCodigoCita();
            Cita nueva = new Cita(cod, "P001", "D002",
                    LocalDate.of(2026, 10, 15), LocalTime.of(15, 30),
                    "Consulta de prueba", EstadoCita.PROGRAMADA);
            citaDAO.insertar(nueva);
            System.out.println("  Insertada: " + cod);

            // ---------- CHOQUE DE HORARIO ----------
            System.out.println("\n--- PROBAR CHOQUE DE HORARIO ---");
            try {
                Cita choque = new Cita(GeneradorCodigo.siguienteCodigoCita(), "P002", "D002",
                        LocalDate.of(2026, 10, 15), LocalTime.of(15, 30),
                        "Intento de choque", EstadoCita.PROGRAMADA);
                citaDAO.insertar(choque);
            } catch (ClinicaException e) {
                System.out.println("  ✅ Excepción esperada: " + e.getMessage());
            }

            // ---------- LISTAR POR DOCTOR ----------
            System.out.println("\n--- CITAS DEL DOCTOR D002 ---");
            citaDAO.listarPorDoctor("D002").forEach(c -> System.out.println("  " + c.getCodigo()));

            // ---------- ELIMINAR LA CITA DE PRUEBA ----------
            System.out.println("\n--- ELIMINAR CITA DE PRUEBA ---");
            citaDAO.eliminar(cod);
            System.out.println("  Eliminada: " + cod);

        } catch (ClinicaException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }

        System.out.println("\n=== PRUEBA FINALIZADA ===");
    }
}