package com.udb.clinica.pruebas;

import com.udb.clinica.conexion.ConexionBD;
import com.udb.clinica.util.LogUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConexion {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE CONEXIÓN JDBC ===\n");

        try (Connection con = ConexionBD.getConexion()) {

            System.out.println("✅ Conectado a: " + con.getCatalog());
            System.out.println("   URL: " + con.getMetaData().getURL());
            System.out.println("   Driver: " + con.getMetaData().getDriverName());
            System.out.println("   Driver Version: " + con.getMetaData().getDriverVersion());

            // Consultar los pacientes para verificar que la BD responde
            System.out.println("\n--- Pacientes en la BD ---");
            String sql = "SELECT codigo_paciente, nombre_completo, tipo_paciente FROM paciente";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                System.out.println(
                    "  " + rs.getString("codigo_paciente")
                    + " | " + rs.getString("nombre_completo")
                    + " | " + rs.getString("tipo_paciente")
                );
            }

            rs.close();
            st.close();

            LogUtil.info("Prueba de conexión exitosa.");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            LogUtil.error("Error en prueba de conexión: " + e.getMessage(), e);
        }
    }
}