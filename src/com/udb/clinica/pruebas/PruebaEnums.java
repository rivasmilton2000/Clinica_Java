package com.udb.clinica.pruebas;

import com.udb.clinica.modelo.enums.*;

public class PruebaEnums {
    public static void main(String[] args) {
        // Probar que los enums se imprimen bonito
        System.out.println("Tipo: " + TipoPaciente.ESTUDIANTE);
        System.out.println("Especialidad: " + Especialidad.PSICOLOGIA);
        System.out.println("Estado: " + EstadoPaciente.ACTIVO);
        System.out.println("Gravedad: " + GravedadPaciente.MODERADO);
        System.out.println("EstadoCita: " + EstadoCita.PROGRAMADA);
        System.out.println("EstadoDoctor: " + EstadoDoctor.ACTIVO);

        // Probar el método desdeBD (simula lo que vendrá de SQL Server)
        TipoPaciente t = TipoPaciente.desdeBD("Docente");
        System.out.println("Convertido desde BD: " + t);

        // Recorrer todos los valores de un enum
        System.out.println("\nTodos los tipos de paciente:");
        for (TipoPaciente tipo : TipoPaciente.values()) {
            System.out.println("  - " + tipo);
        }
    }
}