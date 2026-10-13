// ============================================================
// PAQUETE: com.udb.clinica.vista
// CLASE:   PanelDoctor
// TIPO:    Java Class (extends JPanel)
//
// ¿QUÉ HACE?
// Panel completo de gestión de Doctores con:
//   - Tabla (JTable) con todos los doctores
//   - Formulario con campos de entrada
//   - Botones de CRUD (Nuevo, Guardar, Actualizar, Eliminar, Limpiar)
//
// ¿CÓMO FUNCIONA?
// Sigue el MISMO patrón que PanelPaciente, pero adaptado a Doctor:
//   - Menos campos (no tiene edad ni gravedad)
//   - Usa Especialidad y EstadoDoctor en lugar de Tipo/Estado/Gravedad
//
// NOTA: este panel es una ADAPTACIÓN del PanelPaciente.
// Los conceptos de Swing son idénticos.
// ============================================================

package com.udb.clinica.vista;

import com.udb.clinica.dao.DoctorDAOImpl;
import com.udb.clinica.dao.IDoctorDAO;
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.enums.Especialidad;
import com.udb.clinica.modelo.enums.EstadoDoctor;
import com.udb.clinica.util.GeneradorCodigo;
import com.udb.clinica.util.LogUtil;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/**
 * Panel de gestión de Doctores (CRUD completo).
 */
public class PanelDoctor extends JPanel {

    // ========================================================
    // ATRIBUTOS — DAO
    // ========================================================
    private final IDoctorDAO dao = new DoctorDAOImpl();

    // ========================================================
    // ATRIBUTOS — COMPONENTES DE LA TABLA
    // ========================================================
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // ========================================================
    // ATRIBUTOS — COMPONENTES DEL FORMULARIO
    // ========================================================
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JComboBox<Especialidad> cmbEspecialidad;

    // Estado: Activo/Inactivo
    private JRadioButton rbActivo;
    private JRadioButton rbInactivo;
    private ButtonGroup grupoEstado;

    // ========================================================
    // ATRIBUTOS — BOTONES
    // ========================================================
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // ========================================================
    // ATRIBUTO DE CONTROL
    // ========================================================
    private boolean modoEdicion = false;

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public PanelDoctor() {
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        cargarTabla();
    }

    // ========================================================
    // INICIALIZACIÓN DE COMPONENTES
    // ========================================================
    private void inicializarComponentes() {

        // -------- Tabla --------
        String[] columnas = {"Código", "Nombre", "Especialidad",
                             "Teléfono", "Correo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // Solo lectura
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // -------- Ajuste de anchos --------
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);   // Código
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);  // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(140);  // Especialidad
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);  // Teléfono
        tabla.getColumnModel().getColumn(4).setPreferredWidth(230);  // Correo
        tabla.getColumnModel().getColumn(5).setPreferredWidth(80);   // Estado

        // -------- Campos del formulario --------
        txtCodigo   = new JTextField(10);
        txtNombre   = new JTextField(30);
        txtTelefono = new JTextField(15);
        txtCorreo   = new JTextField(30);

        // Código deshabilitado (se autogenera)
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(235, 235, 235));

        // -------- Combo Especialidad --------
        cmbEspecialidad = new JComboBox<>(Especialidad.values());

        // -------- Estado (radio) --------
        rbActivo   = new JRadioButton("Activo");
        rbInactivo = new JRadioButton("Inactivo");
        grupoEstado = new ButtonGroup();
        grupoEstado.add(rbActivo);
        grupoEstado.add(rbInactivo);
        rbActivo.setSelected(true);

        // -------- Botones --------
        btnNuevo      = new JButton("Nuevo");
        btnGuardar    = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar   = new JButton("Eliminar");
        btnLimpiar    = new JButton("Limpiar");

        Font fuenteBotones = new Font("Segoe UI", Font.PLAIN, 12);
        btnNuevo.setFont(fuenteBotones);
        btnGuardar.setFont(fuenteBotones);
        btnActualizar.setFont(fuenteBotones);
        btnEliminar.setFont(fuenteBotones);
        btnLimpiar.setFont(fuenteBotones);
    }

    // ========================================================
    // LAYOUT
    // ========================================================
    private void configurarLayout() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titulo = new JLabel("Gestión de Doctores");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        JScrollPane scrollTabla = new JScrollPane(tabla);
        add(scrollTabla, BorderLayout.CENTER);

        // Formulario + Botones
        JPanel panelSur = new JPanel(new BorderLayout(10, 10));
        panelSur.add(crearPanelFormulario(), BorderLayout.CENTER);
        panelSur.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Doctor"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Código + Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCodigo, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;
        panel.add(txtNombre, gbc);
        gbc.gridwidth = 1;

        // Fila 1: Especialidad + Teléfono
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(cmbEspecialidad, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 4;
        panel.add(txtTelefono, gbc);

        // Fila 2: Correo
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        panel.add(txtCorreo, gbc);
        gbc.gridwidth = 1;

        // Fila 3: Estado
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        JPanel panelEstado = new JPanel();
        panelEstado.add(rbActivo);
        panelEstado.add(rbInactivo);
        panel.add(panelEstado, gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        return panel;
    }

    // ========================================================
    // EVENTOS
    // ========================================================
    private void configurarEventos() {
        btnNuevo.addActionListener(e -> prepararNuevo());
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionada();
            }
        });
    }

    // ========================================================
    // LÓGICA CRUD
    // ========================================================

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Doctor> lista = dao.listarTodos();

            for (Doctor d : lista) {
                modeloTabla.addRow(new Object[]{
                    d.getCodigo(),
                    d.getNombreCompleto(),
                    d.getEspecialidad().getEtiqueta(),
                    d.getTelefono(),
                    d.getCorreo(),
                    d.getEstadoDoctor().getEtiqueta()
                });
            }

            LogUtil.info("Tabla de doctores cargada: " + lista.size() + " registros.");

        } catch (ClinicaException e) {
            mostrarError("Error al cargar doctores: " + e.getMessage());
        }
    }

    private void prepararNuevo() {
        limpiarFormulario();
        modoEdicion = false;

        try {
            String nuevoCodigo = GeneradorCodigo.siguienteCodigoDoctor();
            txtCodigo.setText(nuevoCodigo);
            txtNombre.requestFocus();
        } catch (ClinicaException e) {
            mostrarError("Error al generar código: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Doctor d = leerFormulario();
            dao.insertar(d);
            mostrarInfo("Doctor guardado con éxito: " + d.getCodigo());
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void actualizar() {
        if (!modoEdicion) {
            mostrarAdvertencia("Seleccione un doctor de la tabla para actualizar.");
            return;
        }

        try {
            Doctor d = leerFormulario();
            dao.actualizar(d);
            mostrarInfo("Doctor actualizado con éxito: " + d.getCodigo());
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al actualizar: " + e.getMessage());
        }
    }

    private void eliminar() {
        if (!modoEdicion) {
            mostrarAdvertencia("Seleccione un doctor de la tabla para eliminar.");
            return;
        }

        String codigo = txtCodigo.getText();
        if (codigo == null || codigo.isEmpty()) {
            mostrarAdvertencia("No hay doctor seleccionado.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar al doctor " + codigo + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resp != JOptionPane.YES_OPTION) return;

        try {
            dao.eliminar(codigo);
            mostrarInfo("Doctor eliminado: " + codigo);
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al eliminar: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        cmbEspecialidad.setSelectedIndex(0);
        rbActivo.setSelected(true);
        modoEdicion = false;
    }

    private Doctor leerFormulario() throws ClinicaException {

        String codigo   = txtCodigo.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo   = txtCorreo.getText().trim();

        if (codigo.isEmpty())   throw new ClinicaException("El código es obligatorio.");
        if (nombre.isEmpty())   throw new ClinicaException("El nombre es obligatorio.");
        if (telefono.isEmpty()) throw new ClinicaException("El teléfono es obligatorio.");
        if (correo.isEmpty())   throw new ClinicaException("El correo es obligatorio.");

        Especialidad especialidad = (Especialidad) cmbEspecialidad.getSelectedItem();

        EstadoDoctor estado = rbActivo.isSelected()
                ? EstadoDoctor.ACTIVO
                : EstadoDoctor.INACTIVO;

        return new Doctor(codigo, nombre, telefono, correo, especialidad, estado);
    }

    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;

        String codigo       = (String) modeloTabla.getValueAt(fila, 0);
        String nombre       = (String) modeloTabla.getValueAt(fila, 1);
        String especialidad = (String) modeloTabla.getValueAt(fila, 2);
        String telefono     = (String) modeloTabla.getValueAt(fila, 3);
        String correo       = (String) modeloTabla.getValueAt(fila, 4);
        String estado       = (String) modeloTabla.getValueAt(fila, 5);

        txtCodigo.setText(codigo);
        txtNombre.setText(nombre);
        txtTelefono.setText(telefono);
        txtCorreo.setText(correo);

        cmbEspecialidad.setSelectedItem(Especialidad.desdeBD(especialidad));

        if ("Activo".equalsIgnoreCase(estado)) {
            rbActivo.setSelected(true);
        } else {
            rbInactivo.setSelected(true);
        }

        modoEdicion = true;
    }

    // ========================================================
    // UTILIDADES DE MENSAJES
    // ========================================================
    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}

//Nestor Osegueda