// ============================================================
// PAQUETE: com.udb.clinica.vista
// CLASE:   VentanaPrincipal
// TIPO:    Java Class (extends JFrame)
//
// ¿QUÉ HACE?
// Es la VENTANA PRINCIPAL de la aplicación.
// Contiene un JTabbedPane con 4 pestañas:
//   - Pacientes
//   - Doctores
//   - Citas
//   - Reportes
//
// ¿POR QUÉ EXISTE?
// - Punto de entrada visual de la app.
// - Coordina todos los paneles.
// - Cierra la conexión a BD al salir.
//
// ¿CÓMO SE USA?
//   VentanaPrincipal.iniciar();    ← método estático
// ============================================================

package com.udb.clinica.vista;

import com.udb.clinica.conexion.ConexionBD;
import com.udb.clinica.util.LogUtil;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la Clínica Universitaria Vida UDB.
 * 
 * <p>Contiene las 4 pestañas del sistema. Al cerrarse, cierra la
 * conexión a la base de datos.</p>
 */
public class VentanaPrincipal extends JFrame {

    // ========================================================
    // ATRIBUTOS
    // ========================================================
    // Guardamos los paneles como atributos para poder acceder
    // a ellos después (por ejemplo, para refrescar la tabla).
    // ========================================================
    private final JTabbedPane tabs;
    private final PanelPaciente panelPaciente;
    private final PanelDoctor   panelDoctor;
    private final PanelCita     panelCita;
    private final PanelReportes panelReportes;

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public VentanaPrincipal() {

        // --------------------------------------------------
        // PASO 1: Configurar el Look and Feel (tema visual).
        // --------------------------------------------------
        configurarLookAndFeel();

        // --------------------------------------------------
        // PASO 2: Configurar la ventana (JFrame).
        // --------------------------------------------------
        setTitle("Clínica Universitaria Vida UDB - Sistema de Gestión de Citas");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Cierre manual
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);                            // Centrar
        setLayout(new BorderLayout());

        // --------------------------------------------------
        // PASO 3: Crear los paneles de cada pestaña.
        // --------------------------------------------------
        // ⚠️ Estas clases deben existir como placeholders.
        // --------------------------------------------------
        panelPaciente = new PanelPaciente();
        panelDoctor   = new PanelDoctor();
        panelCita     = new PanelCita();
        panelReportes = new PanelReportes();

        // --------------------------------------------------
        // PASO 4: Crear el JTabbedPane y agregar pestañas.
        // --------------------------------------------------
        tabs = new JTabbedPane();
        tabs.addTab("Pacientes", panelPaciente);
        tabs.addTab("Doctores",  panelDoctor);
        tabs.addTab("Citas",     panelCita);
        tabs.addTab("Reportes",  panelReportes);

        add(tabs, BorderLayout.CENTER);

        // --------------------------------------------------
        // PASO 5: Manejar el cierre de la ventana.
        // --------------------------------------------------
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarAplicacion();
            }
        });

        // --------------------------------------------------
        // PASO 6: Log de inicio.
        // --------------------------------------------------
        LogUtil.info("Ventana principal abierta.");
    }

    // ========================================================
    // MÉTODOS PRIVADOS
    // ========================================================

    /**
     * Configura el Look and Feel (tema visual) de la aplicación.
     * Preferimos Windows por ser nativo y profesional.
     */
    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            LogUtil.info("Look and Feel: Windows aplicado.");
        } catch (Exception e) {
            LogUtil.warn("No se pudo aplicar el Look and Feel Windows. Usando default.");
        }
    }

    /**
     * Cierra la aplicación de forma limpia:
     * - Cierra la conexión a la BD.
     * - Registra log de cierre.
     * - Sale del programa.
     */
    private void cerrarAplicacion() {
        try {
            LogUtil.info("Cerrando aplicación...");
            ConexionBD.cerrarConexion();
        } catch (Exception e) {
            LogUtil.error("Error al cerrar la conexión", e);
        } finally {
            dispose();
            System.exit(0);
        }
    }

    /**
     * Arranca la aplicación Swing.
     * 
     * ⚠️ IMPORTANTE: TODO componente Swing debe crearse en el
     * hilo EDT (Event Dispatch Thread). Por eso usamos
     * SwingUtilities.invokeLater.
     */
    public static void iniciar() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
            }
        });
    }
}

//Nestor Osegueda