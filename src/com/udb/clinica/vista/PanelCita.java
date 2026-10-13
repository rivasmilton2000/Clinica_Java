// ============================================================
// PAQUETE: com.udb.clinica.vista
// CLASE:   PanelCita
// TIPO:    Java Class (extends JPanel)
//
// ¿QUÉ HACE?
// Panel completo de gestión de Citas con:
//   - Tabla (JTable) con todas las citas
//   - Formulario con combos de paciente/doctor, JSpinner de fecha/hora
//   - Botones de CRUD
//
// ¿POR QUÉ ES DIFERENTE A PACIENTE Y DOCTOR?
// 1. Usa JComboBox<Paciente> y JComboBox<Doctor> (con objetos, no strings).
// 2. Usa JSpinner para fecha y hora.
// 3. Debe cargar los pacientes y doctores desde la BD.
// 4. Debe convertir java.util.Date ↔ LocalDate/LocalTime.
// 5. Valida choques de horario en el DAO (ya implementado).
//
// ⚠️ REQUISITOS EN LOS MODELOS:
// - Paciente debe tener: getDescripcionCorta()
// - Doctor debe tener:   getDescripcionCorta()
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
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.enums.EstadoCita;
import com.udb.clinica.util.GeneradorCodigo;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Panel de gestión de Citas (CRUD completo).
 */
public class PanelCita extends JPanel {

    // ========================================================
    // ATRIBUTOS — DAOs
    // ========================================================
    private final ICitaDAO     citaDAO     = new CitaDAOImpl();
    private final IPacienteDAO pacienteDAO = new PacienteDAOImpl();
    private final IDoctorDAO   doctorDAO   = new DoctorDAOImpl();

    // ========================================================
    // ATRIBUTOS — COMPONENTES DE LA TABLA
    // ========================================================
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // ========================================================
    // ATRIBUTOS — COMPONENTES DEL FORMULARIO
    // ========================================================
    private JTextField txtCodigo;
    private JComboBox<Paciente> cmbPaciente;
    private JComboBox<Doctor>   cmbDoctor;
    private JSpinner            spinnerFecha;
    private JSpinner            spinnerHora;
    private JTextField          txtMotivo;
    private JComboBox<EstadoCita> cmbEstado;

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
    public PanelCita() {
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        cargarCombos();    // Primero combos (necesitan datos)
        cargarTabla();     // Después tabla
    }

    // ========================================================
    // INICIALIZACIÓN DE COMPONENTES
    // ========================================================
    private void inicializarComponentes() {

        // -------- Tabla --------
        String[] columnas = {"Código", "Fecha", "Hora", "Paciente",
                             "Doctor", "Motivo", "Estado"};
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
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);  // Fecha
        tabla.getColumnModel().getColumn(2).setPreferredWidth(70);   // Hora
        tabla.getColumnModel().getColumn(3).setPreferredWidth(180);  // Paciente
        tabla.getColumnModel().getColumn(4).setPreferredWidth(200);  // Doctor
        tabla.getColumnModel().getColumn(5).setPreferredWidth(200);  // Motivo
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);  // Estado

        // -------- Campos del formulario --------
        txtCodigo = new JTextField(10);
        txtMotivo = new JTextField(40);
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(235, 235, 235));

        // -------- Combo Paciente --------
        cmbPaciente = new JComboBox<>();
        cmbPaciente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Paciente) {
                    setText(((Paciente) value).getDescripcionCorta());
                }
                return this;
            }
        });

        // -------- Combo Doctor --------
        cmbDoctor = new JComboBox<>();
        cmbDoctor.setRenderer(new DefaultListCellRenderer() {
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

        // -------- Spinner Fecha --------
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spinnerFecha, "yyyy-MM-dd");
        spinnerFecha.setEditor(editorFecha);

        // -------- Spinner Hora --------
        spinnerHora = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorHora = new JSpinner.DateEditor(spinnerHora, "HH:mm");
        spinnerHora.setEditor(editorHora);

        // -------- Combo Estado --------
        cmbEstado = new JComboBox<>(EstadoCita.values());

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
        JLabel titulo = new JLabel("Gestión de Citas");
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
        panel.setBorder(BorderFactory.createTitledBorder("Datos de la Cita"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Código
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCodigo, gbc);

        // Fila 1: Paciente + Doctor
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbPaciente.setPreferredSize(new Dimension(280, 25));
        panel.add(cmbPaciente, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        panel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 4; gbc.gridwidth = 2;
        cmbDoctor.setPreferredSize(new Dimension(320, 25));
        panel.add(cmbDoctor, gbc);
        gbc.gridwidth = 1;

        // Fila 2: Fecha + Hora + Estado
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1;
        panel.add(spinnerFecha, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Hora:"), gbc);
        gbc.gridx = 3;
        panel.add(spinnerHora, gbc);
        gbc.gridx = 4;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 5;
        panel.add(cmbEstado, gbc);

        // Fila 3: Motivo
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        panel.add(txtMotivo, gbc);

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
    // CARGA DE COMBOS Y TABLA
    // ========================================================

    /**
     * Carga los combos de paciente y doctor desde la BD.
     */
    private void cargarCombos() {
        try {
            // Cargar pacientes
            cmbPaciente.removeAllItems();
            List<Paciente> pacientes = pacienteDAO.listarTodos();
            for (Paciente p : pacientes) {
                cmbPaciente.addItem(p);
            }

            // Cargar doctores
            cmbDoctor.removeAllItems();
            List<Doctor> doctores = doctorDAO.listarTodos();
            for (Doctor d : doctores) {
                cmbDoctor.addItem(d);
            }

            LogUtil.info("Combos cargados: " + pacientes.size() + " pacientes, "
                    + doctores.size() + " doctores.");

        } catch (ClinicaException e) {
            mostrarError("Error al cargar combos: " + e.getMessage());
        }
    }

    /**
     * Carga todas las citas de la BD en la tabla.
     */
    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Cita> lista = citaDAO.listarTodos();

            for (Cita c : lista) {
                modeloTabla.addRow(new Object[]{
                    c.getCodigo(),
                    c.getFechaCita().toString(),
                    c.getHoraCita().toString(),
                    c.getNombrePaciente(),
                    c.getNombreDoctor(),
                    c.getMotivoConsulta(),
                    c.getEstadoCita().getEtiqueta()
                });
            }

            LogUtil.info("Tabla de citas cargada: " + lista.size() + " registros.");

        } catch (ClinicaException e) {
            mostrarError("Error al cargar citas: " + e.getMessage());
        }
    }

    // ========================================================
    // LÓGICA CRUD
    // ========================================================

    private void prepararNuevo() {
        limpiarFormulario();
        modoEdicion = false;

        try {
            String nuevoCodigo = GeneradorCodigo.siguienteCodigoCita();
            txtCodigo.setText(nuevoCodigo);
        } catch (ClinicaException e) {
            mostrarError("Error al generar código: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            Cita c = leerFormulario();
            citaDAO.insertar(c);
            mostrarInfo("Cita guardada con éxito: " + c.getCodigo());
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private void actualizar() {
        if (!modoEdicion) {
            mostrarAdvertencia("Seleccione una cita de la tabla para actualizar.");
            return;
        }

        try {
            Cita c = leerFormulario();
            citaDAO.actualizar(c);
            mostrarInfo("Cita actualizada con éxito: " + c.getCodigo());
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al actualizar: " + e.getMessage());
        }
    }

    private void eliminar() {
        if (!modoEdicion) {
            mostrarAdvertencia("Seleccione una cita de la tabla para eliminar.");
            return;
        }

        String codigo = txtCodigo.getText();
        if (codigo == null || codigo.isEmpty()) {
            mostrarAdvertencia("No hay cita seleccionada.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar la cita " + codigo + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resp != JOptionPane.YES_OPTION) return;

        try {
            citaDAO.eliminar(codigo);
            mostrarInfo("Cita eliminada: " + codigo);
            cargarTabla();
            limpiarFormulario();
            modoEdicion = false;
        } catch (ClinicaException e) {
            mostrarError("Error al eliminar: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtMotivo.setText("");
        if (cmbPaciente.getItemCount() > 0) cmbPaciente.setSelectedIndex(0);
        if (cmbDoctor.getItemCount() > 0)   cmbDoctor.setSelectedIndex(0);
        if (cmbEstado.getItemCount() > 0)   cmbEstado.setSelectedIndex(0);

        // Resetear spinners a "ahora"
        spinnerFecha.setValue(new Date());
        spinnerHora.setValue(new Date());

        modoEdicion = false;
    }

    /**
     * Lee los datos del formulario y construye un objeto Cita.
     */
    private Cita leerFormulario() throws ClinicaException {

        String codigo = txtCodigo.getText().trim();
        String motivo = txtMotivo.getText().trim();

        if (codigo.isEmpty()) throw new ClinicaException("El código es obligatorio.");
        if (motivo.isEmpty()) throw new ClinicaException("El motivo es obligatorio.");

        // Validar selección de paciente y doctor
        Paciente paciente = (Paciente) cmbPaciente.getSelectedItem();
        Doctor   doctor   = (Doctor)   cmbDoctor.getSelectedItem();

        if (paciente == null) throw new ClinicaException("Debe seleccionar un paciente.");
        if (doctor   == null) throw new ClinicaException("Debe seleccionar un doctor.");

        // Convertir java.util.Date → LocalDate y LocalTime
        Date fechaJava = (Date) spinnerFecha.getValue();
        Date horaJava  = (Date) spinnerHora.getValue();

        LocalDate fecha = fechaJava.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalTime hora = horaJava.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0)
                .withNano(0);

        EstadoCita estado = (EstadoCita) cmbEstado.getSelectedItem();

        return new Cita(codigo, paciente.getCodigo(), doctor.getCodigo(),
                        fecha, hora, motivo, estado);
    }

    /**
     * Al seleccionar una fila, carga los datos en el formulario.
     */
    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;

        String codigo   = (String) modeloTabla.getValueAt(fila, 0);
        String fechaStr = (String) modeloTabla.getValueAt(fila, 1);
        String horaStr  = (String) modeloTabla.getValueAt(fila, 2);
        String motivo   = (String) modeloTabla.getValueAt(fila, 5);

        txtCodigo.setText(codigo);
        txtMotivo.setText(motivo);

        // Cargar fecha/hora a los spinners
        try {
            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime hora  = LocalTime.parse(horaStr);

            Date fechaJava = java.sql.Date.valueOf(fecha);
            Date horaJava  = java.sql.Time.valueOf(hora);

            spinnerFecha.setValue(fechaJava);
            spinnerHora.setValue(horaJava);
        } catch (Exception e) {
            LogUtil.warn("No se pudo parsear fecha/hora: " + e.getMessage());
        }

        // Buscar y seleccionar el paciente y doctor por código
        try {
            Cita c = citaDAO.buscarPorCodigo(codigo);

            for (int i = 0; i < cmbPaciente.getItemCount(); i++) {
                if (cmbPaciente.getItemAt(i).getCodigo().equals(c.getCodigoPaciente())) {
                    cmbPaciente.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < cmbDoctor.getItemCount(); i++) {
                if (cmbDoctor.getItemAt(i).getCodigo().equals(c.getCodigoDoctor())) {
                    cmbDoctor.setSelectedIndex(i);
                    break;
                }
            }

            cmbEstado.setSelectedItem(c.getEstadoCita());

        } catch (ClinicaException e) {
            mostrarError("Error al cargar cita: " + e.getMessage());
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