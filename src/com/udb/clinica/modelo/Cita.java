// ============================================================
// PAQUETE: com.udb.clinica.modelo
// CLASE:   Cita
//
// ¿QUÉ HACE?
// Representa una cita médica entre un paciente y un doctor.
//
// ¿POR QUÉ NO HEREDA DE Persona?
// Porque una cita NO es una persona. Es un EVENTO.
// En cambio, una cita ASOCIA a un paciente y un doctor.
//
// ASOCIACIÓN:
// Guardamos los CÓDIGOS de paciente y doctor (como en la BD).
// Son las Foreign Keys (FK) desde el punto de vista de Java.
// ============================================================

package com.udb.clinica.modelo;

import com.udb.clinica.modelo.enums.EstadoCita;
import java.time.LocalDate;   // Fecha (año-mes-día) moderna de Java
import java.time.LocalTime;   // Hora (hora:min:seg) moderna de Java

/**
 * Clase Cita.
 */
public class Cita {

    // ========================================================
    // ATRIBUTOS
    // ========================================================
    private String codigo;
    private String codigoPaciente;   // FK lógica hacia Paciente
    private String codigoDoctor;     // FK lógica hacia Doctor
    private LocalDate fechaCita;     // Ej: 2026-09-25
    private LocalTime horaCita;      // Ej: 09:00
    private String motivoConsulta;
    private EstadoCita estadoCita;

    // ========================================================
    // CAMPOS AUXILIARES (NO van a la BD)
    // ========================================================
    // ¿Para qué sirven?
    // Para mostrar en tablas/reportes el NOMBRE del paciente
    // y del doctor sin tener que hacer otra consulta SQL.
    // 
    // Ejemplo: en vez de mostrar "P001" y "D001" al usuario,
    // mostramos "Juan Pérez" y "Dr. Sánchez".
    // 
    // El DAO las llenará con un JOIN.
    // ========================================================
    private String nombrePaciente;
    private String nombreDoctor;

    // ========================================================
    // CONSTRUCTOR VACÍO
    // ========================================================
    // Por defecto, una cita nueva está PROGRAMADA.
    // ========================================================
    public Cita() {
        this.estadoCita = EstadoCita.PROGRAMADA;
    }

    // ========================================================
    // CONSTRUCTOR COMPLETO (sin los auxiliares)
    // ========================================================
    public Cita(String codigo, String codigoPaciente, String codigoDoctor,
                LocalDate fechaCita, LocalTime horaCita,
                String motivoConsulta, EstadoCita estadoCita) {
        this.codigo = codigo;
        this.codigoPaciente = codigoPaciente;
        this.codigoDoctor = codigoDoctor;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.motivoConsulta = motivoConsulta;
        this.estadoCita = estadoCita;
    }

    // ========================================================
    // GETTERS Y SETTERS
    // ========================================================

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCodigoPaciente() { return codigoPaciente; }
    public void setCodigoPaciente(String codigoPaciente) { this.codigoPaciente = codigoPaciente; }

    public String getCodigoDoctor() { return codigoDoctor; }
    public void setCodigoDoctor(String codigoDoctor) { this.codigoDoctor = codigoDoctor; }

    public LocalDate getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDate fechaCita) { this.fechaCita = fechaCita; }

    public LocalTime getHoraCita() { return horaCita; }
    public void setHoraCita(LocalTime horaCita) { this.horaCita = horaCita; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public EstadoCita getEstadoCita() { return estadoCita; }
    public void setEstadoCita(EstadoCita estadoCita) { this.estadoCita = estadoCita; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getNombreDoctor() { return nombreDoctor; }
    public void setNombreDoctor(String nombreDoctor) { this.nombreDoctor = nombreDoctor; }

    // ========================================================
    // toString()
    // ========================================================
    @Override
    public String toString() {
        return String.format("Cita [%s] %s %s | Pac: %s | Doc: %s | Estado: %s",
                codigo, fechaCita, horaCita, codigoPaciente, codigoDoctor, estadoCita);
    }
}

//Nestor Osegueda