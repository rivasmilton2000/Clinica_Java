// ============================================================
// PAQUETE: com.udb.clinica.vista
// CLASE:   PanelReportes
// TIPO:    Java Class (extends JPanel)
//
// ¿QUÉ HACE?
// Panel con 3 reportes en pestañas internas:
//   R1: Citas por doctor en rango de fechas.
//   R2: Pacientes agrupados por tipo.
//   R3: Citas agrupadas por estado.
//
// ¿CÓMO SE USA?
// 1. Seleccionar la pestaña del reporte.
// 2. R1: Elegir doctor y rango de fechas → "Generar".
// 3. R2 y R3: Se cargan automáticamente al abrir el panel.
// ============================================================

package com.udb.clinica.vista;

import com.udb.clinica.dao.CitaDAOImpl;
import com.udb.clinica.dao.DoctorDAOImpl;
import com.udb.clinica.dao.ICitaDAO;
import com.udb.clinica.dao.IDoctorDAO;
import com.udb.clinica.dao.IPacienteDAO;
import com.udb.clinica.dao.PacienteDAOImpl;
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.util.LogUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Panel con 3 reportes en pestañas internas.
 */
public class PanelReportes extends JPanel {

    // ========================================================
    // ATRIBUTOS — DAOs
    // ========================================================
    private final ICitaDAO     citaDAO     = new CitaDAOImpl();
    private final IPacienteDAO pacienteDAO = new PacienteDAOImpl();
    private final IDoctorDAO   doctorDAO   = new DoctorDAOImpl();

    // ========================================================
    // ATRIBUTOS — PESTAÑA R1 (Citas por doctor)
    // ========================================================
    private JComboBox<Doctor> cmbR1Doctor;
    private JSpinner          spR1Desde;
    private JSpinner          spR1Hasta;
    private JButton           btnR1Generar;
    private JTable            tblR1;
    private DefaultTableModel modeloR1;
    private JLabel            lblR1Total;

    // ========================================================
    // ATRIBUTOS — PESTAÑA R2 (Pacientes por tipo)
    // ========================================================
    private JTable            tblR2;
    private DefaultTableModel modeloR2;
    private JLabel            lblR2Total;

    // ========================================================
    // ATRIBUTOS — PESTAÑA R3 (Citas por estado)
    // ========================================================
    private JTable            tblR3;
    private DefaultTableModel modeloR3;
    private JLabel            lblR3Total;

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public PanelReportes() {
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        cargarR2();     // Los reportes R2 y R3 se cargan automáticamente
        cargarR3();
    }

    // ========================================================
    // INICIALIZACIÓN
    // ========================================================
    private void inicializarComponentes() {

        // ============== R1: Citas por doctor ==============

        cmbR1Doctor = new JComboBox<>();
        cmbR1Doctor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor) {
                    setText(((Doctor) value).getDescripcionCorta());
                }
                return this;
            }
        });
        cmbR1Doctor.setPreferredSize(new Dimension(320, 25));

        // Spinners de fecha (desde/hasta)
        spR1Desde = new JSpinner(new SpinnerDateModel());
        spR1Desde.setEditor(new JSpinner.DateEditor(spR1Desde, "yyyy-MM-dd"));

        spR1Hasta = new JSpinner(new SpinnerDateModel());
        spR1Hasta.setEditor(new JSpinner.DateEditor(spR1Hasta, "yyyy-MM-dd"));

        btnR1Generar = new JButton("Generar Reporte");

        modeloR1 = new DefaultTableModel(
                new String[]{"Código", "Fecha", "Hora", "Paciente", "Motivo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblR1 = new JTable(modeloR1);
        tblR1.setRowHeight(24);
        tblR1.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblR1.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblR1.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblR1.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblR1.getColumnModel().getColumn(4).setPreferredWidth(220);
        tblR1.getColumnModel().getColumn(5).setPreferredWidth(100);

        lblR1Total = new JLabel("Total: 0");
        lblR1Total.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // ============== R2: Pacientes por tipo ==============

        modeloR2 = new DefaultTableModel(new String[]{"Tipo", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblR2 = new JTable(modeloR2);
        tblR2.setRowHeight(24);
        tblR2.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblR2.getColumnModel().getColumn(1).setPreferredWidth(100);

        lblR2Total = new JLabel("Total: 0");
        lblR2Total.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // ============== R3: Citas por estado ==============

        modeloR3 = new DefaultTableModel(new String[]{"Estado", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblR3 = new JTable(modeloR3);
        tblR3.setRowHeight(24);
        tblR3.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblR3.getColumnModel().getColumn(1).setPreferredWidth(100);

        lblR3Total = new JLabel("Total: 0");
        lblR3Total.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    // ========================================================
    // LAYOUT
    // ========================================================
    private void configurarLayout() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titulo = new JLabel("Reportes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // JTabbedPane interno con 3 pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("R1: Citas por Doctor", crearPanelR1());
        tabs.addTab("R2: Pacientes por Tipo", crearPanelR2());
        tabs.addTab("R3: Ocupación de la Clínica", crearPanelR3());

        add(tabs, BorderLayout.CENTER);
    }

    // -------- Panel R1 --------
    private JPanel crearPanelR1() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filtros arriba
        JPanel filtros = new JPanel(new GridBagLayout());
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        filtros.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        filtros.add(cmbR1Doctor, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        filtros.add(new JLabel("Desde:"), gbc);
        gbc.gridx = 1;
        filtros.add(spR1Desde, gbc);
        gbc.gridx = 2;
        filtros.add(new JLabel("Hasta:"), gbc);
        gbc.gridx = 3;
        filtros.add(spR1Hasta, gbc);

        gbc.gridx = 4; gbc.gridy = 1;
        filtros.add(btnR1Generar, gbc);

        panel.add(filtros, BorderLayout.NORTH);

        // Tabla al centro
        panel.add(new JScrollPane(tblR1), BorderLayout.CENTER);

        // Total abajo
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(lblR1Total);
        panel.add(panelTotal, BorderLayout.SOUTH);

        return panel;
    }

    // -------- Panel R2 --------
    private JPanel crearPanelR2() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel desc = new JLabel("Cantidad de pacientes agrupados por tipo:");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(desc, BorderLayout.NORTH);

        panel.add(new JScrollPane(tblR2), BorderLayout.CENTER);

        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(lblR2Total);
        panel.add(panelTotal, BorderLayout.SOUTH);

        return panel;
    }

    // -------- Panel R3 --------
    private JPanel crearPanelR3() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel desc = new JLabel("Cantidad de citas agrupadas por estado:");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(desc, BorderLayout.NORTH);

        panel.add(new JScrollPane(tblR3), BorderLayout.CENTER);

        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(lblR3Total);
        panel.add(panelTotal, BorderLayout.SOUTH);

        return panel;
    }

    // ========================================================
    // EVENTOS
    // ========================================================
    private void configurarEventos() {

        // Cargar combos de doctores al inicializar
        cargarDoctoresCombo();

        // Click en "Generar Reporte"
        btnR1Generar.addActionListener(e -> cargarR1());
    }

    // ========================================================
    // CARGA DE DATOS — R1
    // ========================================================
    private void cargarDoctoresCombo() {
        try {
            cmbR1Doctor.removeAllItems();
            List<Doctor> doctores = doctorDAO.listarTodos();
            for (Doctor d : doctores) {
                cmbR1Doctor.addItem(d);
            }
        } catch (ClinicaException e) {
            mostrarError("Error al cargar doctores: " + e.getMessage());
        }
    }

    private void cargarR1() {
        try {
            Doctor doctor = (Doctor) cmbR1Doctor.getSelectedItem();
            if (doctor == null) {
                mostrarAdvertencia("Seleccione un doctor.");
                return;
            }

            Date fechaDesde = (Date) spR1Desde.getValue();
            Date fechaHasta = (Date) spR1Hasta.getValue();

            LocalDate desde = fechaDesde.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = fechaHasta.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            if (desde.isAfter(hasta)) {
                mostrarAdvertencia("La fecha 'desde' no puede ser mayor que 'hasta'.");
                return;
            }

            // Limpiar tabla
            modeloR1.setRowCount(0);

            // Consultar
            List<Cita> lista = citaDAO.listarPorDoctorYRango(doctor.getCodigo(), desde, hasta);

            for (Cita c : lista) {
                modeloR1.addRow(new Object[]{
                    c.getCodigo(),
                    c.getFechaCita().toString(),
                    c.getHoraCita().toString(),
                    c.getNombrePaciente(),
                    c.getMotivoConsulta(),
                    c.getEstadoCita().getEtiqueta()
                });
            }

            lblR1Total.setText("Total: " + lista.size() + " citas");

        } catch (ClinicaException e) {
            mostrarError("Error al generar reporte: " + e.getMessage());
        }
    }

    // ========================================================
    // CARGA DE DATOS — R2
    // ========================================================
    private void cargarR2() {
        try {
            modeloR2.setRowCount(0);
            List<Object[]> lista = pacienteDAO.contarPorTipo();

            int granTotal = 0;
            for (Object[] fila : lista) {
                String tipo = (String) fila[0];
                int total   = (int)    fila[1];
                modeloR2.addRow(new Object[]{ tipo, total });
                granTotal += total;
            }

            lblR2Total.setText("Total: " + granTotal + " pacientes");

        } catch (ClinicaException e) {
            mostrarError("Error al cargar reporte de pacientes: " + e.getMessage());
        }
    }

    // ========================================================
    // CARGA DE DATOS — R3
    // ========================================================
    private void cargarR3() {
        try {
            modeloR3.setRowCount(0);
            List<Object[]> lista = citaDAO.contarPorEstado();

            int granTotal = 0;
            for (Object[] fila : lista) {
                String estado = (String) fila[0];
                int total     = (int)    fila[1];
                modeloR3.addRow(new Object[]{ estado, total });
                granTotal += total;
            }

            lblR3Total.setText("Total: " + granTotal + " citas");

        } catch (ClinicaException e) {
            mostrarError("Error al cargar reporte de citas: " + e.getMessage());
        }
    }

    // ========================================================
    // UTILIDADES
    // ========================================================
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}

//Nestor Osegueda