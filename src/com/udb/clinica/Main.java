// ============================================================
// PAQUETE: com.udb.clinica
// CLASE:   Main
// TIPO:    Java Main Class
// 
// ⭐ ESTA ES LA CLASE PRINCIPAL DEL PROYECTO ⭐
//
// ¿QUÉ HACE?
// Punto de entrada de la aplicación.
// Es la primera clase que se ejecuta.
//
// ¿POR QUÉ ESTÁ EN EL PAQUETE RAÍZ?
// Convención profesional: Main siempre en el paquete raíz.
//
// ¿CÓMO SE EJECUTA?
//   Shift + F6 sobre este archivo,
//   o configurarlo como Main Class del proyecto (F6).
// ============================================================

package com.udb.clinica;

import com.udb.clinica.util.LogUtil;
import com.udb.clinica.vista.VentanaPrincipal;

/**
 * Punto de entrada del sistema Clínica Universitaria Vida UDB.
 */
public class Main {

    public static void main(String[] args) {

        // --------------------------------------------------
        // PASO 1: Registrar en log el arranque de la app.
        // --------------------------------------------------
        LogUtil.info("========== INICIANDO CLÍNICA UDB ==========");

        // --------------------------------------------------
        // PASO 2: Arrancar la interfaz gráfica.
        // --------------------------------------------------
        // Delegamos a VentanaPrincipal.iniciar() para que
        // se ejecute en el hilo EDT de Swing.
        // --------------------------------------------------
        VentanaPrincipal.iniciar();

        // --------------------------------------------------
        // El programa se queda corriendo hasta que el usuario
        // cierra la ventana. El cierre llama a
        // ConexionBD.cerrarConexion() automáticamente.
        // --------------------------------------------------
    }
}

//Nestor Osegueda