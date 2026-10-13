package com.udb.clinica.pruebas;

import com.udb.clinica.dao.IPacienteDAO;
import com.udb.clinica.dao.PacienteDAOImpl;
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.enums.*;
import com.udb.clinica.util.GeneradorCodigo;
import com.udb.clinica.util.LogUtil;

import java.util.List;

public class PruebaPacienteDAO {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE PacienteDAO ===\n");

        IPacienteDAO dao = new PacienteDAOImpl();

        try {
            // -------- 1. LISTAR TODOS --------
            System.out.println("--- 1. LISTAR TODOS ---");
            List<Paciente> lista = dao.listarTodos();
            for (Paciente p : lista) {
                System.out.println("  " + p);
            }

            // -------- 2. BUSCAR POR CÓDIGO --------
            System.out.println("\n--- 2. BUSCAR P001 ---");
            Paciente p1 = dao.buscarPorCodigo("P001");
            System.out.println("  " + p1);

            // -------- 3. INSERTAR --------
            System.out.println("\n--- 3. INSERTAR NUEVO PACIENTE ---");
            String nuevoCodigo = GeneradorCodigo.siguienteCodigoPaciente();
            Paciente nuevo = new Paciente(
                    nuevoCodigo, "Carlos Test", "7777-9999",
                    "carlos.test@udb.edu.sv", 30,
                    TipoPaciente.ADMINISTRATIVO,
                    EstadoPaciente.ACTIVO,
                    GravedadPaciente.MODERADO
            );
            dao.insertar(nuevo);
            System.out.println("  Insertado: " + nuevoCodigo);

            // -------- 4. BUSCAR EL INSERTADO --------
            System.out.println("\n--- 4. BUSCAR " + nuevoCodigo + " ---");
            Paciente insertado = dao.buscarPorCodigo(nuevoCodigo);
            System.out.println("  " + insertado);

            // -------- 5. ACTUALIZAR --------
            System.out.println("\n--- 5. ACTUALIZAR ---");
            insertado.setGravedad(GravedadPaciente.GRAVE);
            insertado.setEdad(31);
            dao.actualizar(insertado);
            System.out.println("  Actualizado: " + dao.buscarPorCodigo(nuevoCodigo));

            // -------- 6. ELIMINAR --------
            System.out.println("\n--- 6. ELIMINAR " + nuevoCodigo + " ---");
            dao.eliminar(nuevoCodigo);
            System.out.println("  Eliminado OK");

            // -------- 7. BUSCAR ELIMINADO (debe fallar) --------
            System.out.println("\n--- 7. BUSCAR ELIMINADO (debe fallar) ---");
            try {
                dao.buscarPorCodigo(nuevoCodigo);
            } catch (Exception e) {
                System.out.println("  ✅ Excepción esperada: " + e.getMessage());
            }

        } catch (ClinicaException e) {
            System.err.println("❌ Error: " + e.getMessage());
            LogUtil.error("Error en prueba PacienteDAO", e);
        }

        System.out.println("\n=== PRUEBA FINALIZADA ===");
    }
}