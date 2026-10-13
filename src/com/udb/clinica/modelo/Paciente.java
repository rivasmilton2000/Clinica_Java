// ============================================================
// PAQUETE: com.udb.clinica.modelo
// CLASE:   Paciente
//
// ¿QUÉ HACE?
// Representa a un paciente de la clínica.
// 
// HERENCIA:
// "extends Persona" → Paciente ES UNA Persona.
// Hereda: codigo, nombreCompleto, telefono, correo.
// 
// AÑADE:
// edad, tipoPaciente, estadoPaciente, gravedad.
// ============================================================

package com.udb.clinica.modelo;

import com.udb.clinica.modelo.enums.EstadoPaciente;
import com.udb.clinica.modelo.enums.GravedadPaciente;
import com.udb.clinica.modelo.enums.TipoPaciente;

/**
 * Subclase Paciente.
 */
public class Paciente extends Persona {

    // ========================================================
    // ATRIBUTOS PROPIOS DEL PACIENTE
    // ========================================================
    private int edad;
    private TipoPaciente tipoPaciente;
    private EstadoPaciente estadoPaciente;
    private GravedadPaciente gravedad;

    // ========================================================
    // CONSTRUCTOR VACÍO
    // ========================================================
    public Paciente() {
        super();
        this.estadoPaciente = EstadoPaciente.ACTIVO;
        this.gravedad = GravedadPaciente.LEVE;
    }

    // ========================================================
    // CONSTRUCTOR COMPLETO
    // ========================================================
    public Paciente(String codigo, String nombreCompleto, String telefono, String correo,
                    int edad, TipoPaciente tipoPaciente, EstadoPaciente estadoPaciente,
                    GravedadPaciente gravedad) {
        super(codigo, nombreCompleto, telefono, correo);
        this.edad = edad;
        this.tipoPaciente = tipoPaciente;
        this.estadoPaciente = estadoPaciente;
        this.gravedad = gravedad;
    }

    // ========================================================
    // GETTERS Y SETTERS
    // ========================================================
    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public TipoPaciente getTipoPaciente() {
        return tipoPaciente;
    }

    public void setTipoPaciente(TipoPaciente tipoPaciente) {
        this.tipoPaciente = tipoPaciente;
    }

    public EstadoPaciente getEstadoPaciente() {
        return estadoPaciente;
    }

    public void setEstadoPaciente(EstadoPaciente estadoPaciente) {
        this.estadoPaciente = estadoPaciente;
    }

    public GravedadPaciente getGravedad() {
        return gravedad;
    }

    public void setGravedad(GravedadPaciente gravedad) {
        this.gravedad = gravedad;
    }

    // ========================================================
    // IMPLEMENTACIÓN DEL MÉTODO ABSTRACTO getRol()
    // ========================================================
    @Override
    public String getRol() {
        return "Paciente";
    }

    // ========================================================
    // toString() SOBRESCRITO
    // ========================================================
    @Override
    public String toString() {
        return super.toString() + " | Edad: " + edad + " | Tipo: " + tipoPaciente
                + " | Estado: " + estadoPaciente + " | Gravedad: " + gravedad;
    }

    // ========================================================
    // MÉTODO AUXILIAR PARA COMBOBOX
    // ========================================================
    /**
     * Representación CORTA del paciente para usar en ComboBox.
     * Formato: "P001 - Juan Pérez López"
     * 
     * @return String con código y nombre.
     */
    public String getDescripcionCorta() {
        return getCodigo() + " - " + getNombreCompleto();
    }
}

//Nestor Osegueda