package com.udb.clinica.pruebas;

import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.util.Validador;

public class PruebaValidador {
    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE VALIDACIÓN ===\n");

        // ---------- Correo ----------
        System.out.println("--- Correos ---");
        probarCorreo("juan@udb.edu.sv");        // ✅
        probarCorreo("maria.gomez@gmail.com");  // ✅
        probarCorreo("correo-malo");            // ❌
        probarCorreo("sin@dominio");            // ❌
        probarCorreo("");                       // ❌

        // ---------- Teléfono ----------
        System.out.println("\n--- Teléfonos ---");
        probarTelefono("7777-1234");    // ✅
        probarTelefono("2222-5678");    // ✅
        probarTelefono("77771234");     // ❌ (sin guion)
        probarTelefono("777-1234");     // ❌
        probarTelefono("");             // ❌

        // ---------- Edad ----------
        System.out.println("\n--- Edades ---");
        probarEdad(20);     // ✅
        probarEdad(119);    // ✅
        probarEdad(0);      // ❌
        probarEdad(120);    // ❌
        probarEdad(-5);     // ❌

        // ---------- Requerir (con excepción) ----------
        System.out.println("\n--- Requerir con excepción ---");
        try {
            Validador.requerirCorreoValido("malo@");
        } catch (ClinicaException e) {
            System.out.println("✅ Excepción capturada: " + e.getMessage());
        }
    }

    static void probarCorreo(String correo) {
        boolean ok = Validador.esCorreoValido(correo);
        System.out.println("  " + (ok ? "✅" : "❌") + " Correo: '" + correo + "' → " + ok);
    }

    static void probarTelefono(String tel) {
        boolean ok = Validador.esTelefonoValido(tel);
        System.out.println("  " + (ok ? "✅" : "❌") + " Teléfono: '" + tel + "' → " + ok);
    }

    static void probarEdad(int edad) {
        boolean ok = Validador.esEdadValida(edad);
        System.out.println("  " + (ok ? "✅" : "❌") + " Edad: " + edad + " → " + ok);
    }
}