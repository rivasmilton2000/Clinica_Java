// ============================================================
// PAQUETE: com.udb.clinica.vista
// CLASE:   PanelPaciente
// TIPO:    Java Class (extends JPanel)
//
// ¿QUÉ HACE?
// Panel completo de gestión de Pacientes con:
//   - Tabla (JTable) con todos los pacientes
//   - Formulario con campos de entrada
//   - Botones de CRUD (Guardar, Actualizar, Eliminar, Limpiar)
//
// ¿CÓMO FUNCIONA?
// 1. Al abrir: se cargan los pacientes de la BD en la tabla.
// 2. Al hacer clic en una fila: los datos se cargan en el formulario.
// 3. Botones:
//    - Nuevo: limpia el formulario y genera un código nuevo.
//    - Guardar: INSERTA el paciente actual.
//    - Actualizar: ACTUALIZA el paciente cargado.
//    - Eliminar: ELIMINA el paciente cargado.
//    - Limpiar: limpia el formulario.
//
// AJUSTES VISUALES APLICADOS:
// - Botones sin colores personalizados (Windows L&F ocultaba el texto).
// - Anchos de columnas ajustados (correo, nombre, teléfono).
// - Panel de gravedad con ancho suficiente.
// ============================================================

package com.udb.clinica.vista;

import com.udb.clinica.dao.IPacienteDAO;
import com.udb.clinica.dao.PacienteDAOImpl;
import com.udb.clinica.excepciones.ClinicaException;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.enums.EstadoPaciente;
import com.udb.clinica.modelo.enums.GravedadPaciente;
import com.udb.clinica.modelo.enums.TipoPaciente;
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
 * Panel de gestión de Pacientes (CRUD completo).
 */
public class PanelPaciente extends JPanel {

    // ========================================================
    // ATRIBUTOS — DAO
    // ========================================================
    private final IPacienteDAO dao = new PacienteDAOImpl();

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
    private JTextField txtEdad;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JComboBox<TipoPaciente> cmbTipo;

    // Estado: Activo/Inactivo
    private JRadioButton rbActivo;
    private JRadioButton rbInactivo;
    private ButtonGroup grupoEstado;

    // Gravedad: Leve/Moderado/Grave
    private JRadioButton rbLeve;
    private JRadioButton rbModerado;
    private JRadioButton rbGrave;
    private ButtonGroup grupoGravedad;

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
    // Indica si el formulario actual corresponde a un paciente
    // existente (para saber si guardar o actualizar).
    private boolean modoEdicion = false;

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public PanelPaciente() {
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        cargarTabla();     // Carga inicial desde la BD
    }

    // ========================================================
    // INICIALIZACIÓN DE COMPONENTES
    // ========================================================
    private void inicializarComponentes() {

        // -------- Tabla --------
        String[] columnas = {"Código", "Nombre", "Edad", "Teléfono", "Correo",
                             "Tipo", "Estado", "Gravedad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // La tabla NO es editable directamente (solo lectura).
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // -------- Ajuste de anchos de columna --------
        // Por defecto, todas las columnas ocupan el mismo ancho, pero
        // algunas (como Correo o Nombre) necesitan más espacio.
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);   // Código
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);  // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(50);   // Edad
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);  // Teléfono
        tabla.getColumnModel().getColumn(4).setPreferredWidth(230);  // Correo (más ancho)
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);  // Tipo
        tabla.getColumnModel().getColumn(6).setPreferredWidth(80);   // Estado
        tabla.getColumnModel().getColumn(7).setPreferredWidth(90);   // Gravedad

        // -------- Campos del formulario --------
        txtCodigo   = new JTextField(10);
        txtNombre   = new JTextField(30);
        txtEdad     = new JTextField(5);
        txtTelefono = new JTextField(15);
        txtCorreo   = new JTextField(30);

        // El código se autogenera, no se edita manualmente.
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(235, 235, 235));

        // -------- Combo Tipo --------
        cmbTipo = new JComboBox<>(TipoPaciente.values());

        // -------- Estado (radio) --------
        rbActivo   = new JRadioButton("Activo");
        rbInactivo = new JRadioButton("Inactivo");
        grupoEstado = new ButtonGroup();
        grupoEstado.add(rbActivo);
        grupoEstado.add(rbInactivo);
        rbActivo.setSelected(true);   // Por defecto: Activo

        // -------- Gravedad (radio con colores) --------
        rbLeve     = new JRadioButton("Leve");
        rbModerado = new JRadioButton("Moderado");
        rbGrave    = new JRadioButton("Grave");

        // Colores personalizados (Foreground SÍ funciona con Windows L&F)
        rbLeve.setForeground(new Color(40, 167, 69));      // Verde
        rbModerado.setForeground(new Color(255, 140, 0));  // Naranja
        rbGrave.setForeground(new Color(220, 53, 69));     // Rojo

        // Negrita para resaltar
        Font negrita = new Font("Segoe UI", Font.BOLD, 12);
        rbLeve.setFont(negrita);
        rbModerado.setFont(negrita);
        rbGrave.setFont(negrita);

        grupoGravedad = new ButtonGroup();
        grupoGravedad.add(rbLeve);
        grupoGravedad.add(rbModerado);
        grupoGravedad.add(rbGrave);
        rbLeve.setSelected(true);     // Por defecto: Leve

        // -------- Botones --------
        // ⚠️ NOTA: NO usamos setBackground() en los botones porque el
        // Look and Feel nativo de Windows oculta el texto cuando se
        // personalizan colores. Los dejamos con el estilo nativo.
        // Sí funciona el setForeground() si se quiere, pero se ve raro.
        btnNuevo      = new JButton("Nuevo");
        btnGuardar    = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar   = new JButton("Eliminar");
        btnLimpiar    = new JButton("Limpiar");

        // Un pequeño ajuste de fuente para que se vean mejor
        Font fuenteBotones = new Font("Segoe UI", Font.PLAIN, 12);
        btnNuevo.setFont(fuenteBotones);
        btnGuardar.setFont(fuenteBotones);
        btnActualizar.setFont(fuenteBotones);
        btnEliminar.setFont(fuenteBotones);
        btnLimpiar.setFont(fuenteBotones);
    }

    // ========================================================
    // LAYOUT — CÓMO SE ACOMODAN LOS COMPONENTES
    // ========================================================
    private void configurarLayout() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // -------- Zona NORTE: título --------
        JLabel titulo = new JLabel("Gestión de Pacientes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // -------- Zona CENTER: tabla --------
        JScrollPane scrollTabla = new JScrollPane(tabla);
        add(scrollTabla, BorderLayout.CENTER);

        // -------- Zona SUR: formulario + botones --------
        JPanel panelSur = new JPanel(new BorderLayout(10, 10));
        panelSur.add(crearPanelFormulario(), BorderLayout.CENTER);
        panelSur.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del formulario (campos).
     * Usa GridBagLayout para alinear etiquetas y campos.
     */
    private JPanel crearPanelFormulario() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);   // Márgenes
        gbc.anchor = GridBagConstraints.WEST;  // Alinear a la izquierda

        // Fila 0: Código + Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCodigo, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3;      // Ocupa 3 columnas
        panel.add(txtNombre, gbc);
        gbc.gridwidth = 1;                     // Restaurar

        // Fila 1: Edad + Teléfono + Correo
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEdad, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 3;
        panel.add(txtTelefono, gbc);
        gbc.gridx = 4;
        panel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 5;
        panel.add(txtCorreo, gbc);

        // Fila 2: Tipo + Estado
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(cmbTipo, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 4; gbc.gridwidth = 2;
        JPanel panelEstado = new JPanel();
        panelEstado.add(rbActivo);
        panelEstado.add(rbInactivo);
        panel.add(panelEstado, gbc);
        gbc.gridwidth = 1;

        // Fila 3: Gravedad
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Gravedad:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        JPanel panelGravedad = new JPanel();
        // ⚠️ AJUSTE: ancho mínimo para que "Leve" no se corte.
        panelGravedad.setPreferredSize(new Dimension(400, 30));
        panelGravedad.add(rbLeve);
        panelGravedad.add(rbModerado);
        panelGravedad.add(rbGrave);
        panel.add(panelGravedad, gbc);

        return panel;
    }

    /**
     * Crea el panel con los botones de acción.
     */
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
    // EVENTOS — QUÉ HACEN LOS BOTONES Y LA TABLA
    // ========================================================
    private void configurarEventos() {

        // ---------- Click en "Nuevo" ----------
        btnNuevo.addActionListener(e -> prepararNuevo());

        // ---------- Click en "Guardar" ----------
        btnGuardar.addActionListener(e -> guardar());

        // ---------- Click en "Actualizar" ----------
        btnActualizar.addActionListener(e -> actualizar());

        // ---------- Click en "Eliminar" ----------
        btnEliminar.addActionListener(e -> eliminar());

        // ---------- Click en "Limpiar" ----------
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // ---------- Selección de fila en tabla ----------
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionada();
            }
        });
    }

    // ========================================================
    // LÓGICA — OPERACIONES CRUD
    // ========================================================

    /**
     * Carga todos los pacientes de la BD en la tabla.
     */
    private void cargarTabla() {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);

            // Obtener lista de la BD
            List<Paciente> lista = dao.listarTodos();

            // Agregar fila por fila
            for (Paciente p : lista) {
                modeloTabla.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombreCompleto(),
                    p.getEdad(),
                    p.getTelefono(),
                    p.getCorreo(),
                    p.getTipoPaciente().getEtiqueta(),
                    p.getEstadoPaciente().getEtiqueta(),
                    p.getGravedad().getEtiqueta()
                });
            }

            LogUtil.info("Tabla de pacientes cargada: " + lista.size() + " registros.");

        } catch (ClinicaException e) {
            mostrarError("Error al cargar pacientes: " + e.getMessage());
        }
    }

    /**
     * Prepara el formulario para crear un nuevo paciente.
     * - Limpia los campos.
     * - Genera un nuevo código automáticamente.
     * - Desactiva el modo edición.
     */
    private void prepararNuevo() {
        limpiarFormulario();
        modoEdicion = false;

        try {
            String nuevoCodigo = GeneradorCodigo.siguienteCodigoPaciente();
            txtCodigo.setText(nuevoCodigo);
            txtNombre.requestFocus();   // Poner el cursor en el nombre
        } catch (ClinicaException e) {
            mostrarError("Error al generar código: " + e.getMessage());
        }
    }

    /**
     * Guarda un nuevo paciente en la BD.
     */
    private void guardar() {
        try {
            Paciente p = leerFormulario();
            dao.insertar(p);
            mostrarInfo("Paciente guardado con éxito: " + p.getCodigo());
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    /**
     * Actualiza un paciente existente en la BD.
     */
    private void actualizar() {
        if (!modoEdicion) {
            mostrarAdvertencia("Seleccione un paciente de la tabla para actualizar.");
            return;
        }

        try {
            Paciente p = leerFormulario();
            dao.actualizar(p);
            mostrarInfo("Paciente actualizado con éxito: " + p.getCodigo());
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al actualizar: " + e.getMessage());
        }
    }

    /**
     * Elimina el paciente actualmente cargado en el formulario.
     */
    private void eliminar() {
        if (!modoEdicion) {
            mostrarAdvertencia("Seleccione un paciente de la tabla para eliminar.");
            return;
        }

        String codigo = txtCodigo.getText();
        if (codigo == null || codigo.isEmpty()) {
            mostrarAdvertencia("No hay paciente seleccionado.");
            return;
        }

        // Confirmación
        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar al paciente " + codigo + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resp != JOptionPane.YES_OPTION) return;

        try {
            dao.eliminar(codigo);
            mostrarInfo("Paciente eliminado: " + codigo);
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al eliminar: " + e.getMessage());
        }
    }

    /**
     * Limpia el formulario y reinicia los valores por defecto.
     */
    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        cmbTipo.setSelectedIndex(0);
        rbActivo.setSelected(true);
        rbLeve.setSelected(true);
        modoEdicion = false;
    }

    /**
     * Lee los datos del formulario y construye un objeto Paciente.
     * 
     * @return Paciente con los datos actuales.
     * @throws ClinicaException si algún dato es inválido.
     */
    private Paciente leerFormulario() throws ClinicaException {

        String codigo   = txtCodigo.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String edadStr  = txtEdad.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo   = txtCorreo.getText().trim();

        // Validar vacíos
        if (codigo.isEmpty())   throw new ClinicaException("El código es obligatorio.");
        if (nombre.isEmpty())   throw new ClinicaException("El nombre es obligatorio.");
        if (edadStr.isEmpty())  throw new ClinicaException("La edad es obligatoria.");
        if (telefono.isEmpty()) throw new ClinicaException("El teléfono es obligatorio.");
        if (correo.isEmpty())   throw new ClinicaException("El correo es obligatorio.");

        // Parsear edad
        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            throw new ClinicaException("La edad debe ser un número válido.");
        }

        // Enums
        TipoPaciente tipo = (TipoPaciente) cmbTipo.getSelectedItem();

        EstadoPaciente estado = rbActivo.isSelected()
                ? EstadoPaciente.ACTIVO
                : EstadoPaciente.INACTIVO;

        GravedadPaciente gravedad;
        if (rbLeve.isSelected())          gravedad = GravedadPaciente.LEVE;
        else if (rbModerado.isSelected()) gravedad = GravedadPaciente.MODERADO;
        else                              gravedad = GravedadPaciente.GRAVE;

        return new Paciente(codigo, nombre, telefono, correo, edad, tipo, estado, gravedad);
    }

    /**
     * Al seleccionar una fila de la tabla, carga sus datos en el formulario.
     */
    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;   // Nada seleccionado

        // Leer del modelo de tabla
        String codigo   = (String) modeloTabla.getValueAt(fila, 0);
        String nombre   = (String) modeloTabla.getValueAt(fila, 1);
        int    edad     = (int)    modeloTabla.getValueAt(fila, 2);
        String telefono = (String) modeloTabla.getValueAt(fila, 3);
        String correo   = (String) modeloTabla.getValueAt(fila, 4);
        String tipo     = (String) modeloTabla.getValueAt(fila, 5);
        String estado   = (String) modeloTabla.getValueAt(fila, 6);
        String gravedad = (String) modeloTabla.getValueAt(fila, 7);

        // Cargar al formulario
        txtCodigo.setText(codigo);
        txtNombre.setText(nombre);
        txtEdad.setText(String.valueOf(edad));
        txtTelefono.setText(telefono);
        txtCorreo.setText(correo);

        // Seleccionar combo
        cmbTipo.setSelectedItem(TipoPaciente.desdeBD(tipo));

        // Estado
        if ("Activo".equalsIgnoreCase(estado)) {
            rbActivo.setSelected(true);
        } else {
            rbInactivo.setSelected(true);
        }

        // Gravedad
        if ("Leve".equalsIgnoreCase(gravedad))           rbLeve.setSelected(true);
        else if ("Moderado".equalsIgnoreCase(gravedad))  rbModerado.setSelected(true);
        else                                             rbGrave.setSelected(true);

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