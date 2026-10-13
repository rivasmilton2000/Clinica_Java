// ============================================================
// PAQUETE: com.udb.clinica.modelo
// CLASE:   Doctor
//
// ¿QUÉ HACE?
// Representa a un doctor de la clínica.
//
// HERENCIA:
// "extends Persona" → Doctor ES UNA Persona.
// Hereda: codigo, nombreCompleto, telefono, correo.
//
// AÑADE:
// especialidad, estadoDoctor.
// ============================================================

package com.udb.clinica.modelo;

import com.udb.clinica.modelo.enums.Especialidad;
import com.udb.clinica.modelo.enums.EstadoDoctor;

/**
 * Subclase Doctor.
 */
public class Doctor extends Persona {

    // ========================================================
    // ATRIBUTOS PROPIOS DEL DOCTOR
    // ========================================================
    private Especialidad especialidad;
    private EstadoDoctor estadoDoctor;

    // ========================================================
    // CONSTRUCTOR VACÍO
    // ========================================================
    public Doctor() {
        super();
        this.estadoDoctor = EstadoDoctor.ACTIVO;
    }

    // ========================================================
    // CONSTRUCTOR COMPLETO
    // ========================================================
    public Doctor(String codigo, String nombreCompleto, String telefono, String correo,
                  Especialidad especialidad, EstadoDoctor estadoDoctor) {
        super(codigo, nombreCompleto, telefono, correo);
        this.especialidad = especialidad;
        this.estadoDoctor = estadoDoctor;
    }

    // ========================================================
    // GETTERS Y SETTERS
    // ========================================================
    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public EstadoDoctor getEstadoDoctor() {
        return estadoDoctor;
    }

    public void setEstadoDoctor(EstadoDoctor estadoDoctor) {
        this.estadoDoctor = estadoDoctor;
    }

    // ========================================================
    // IMPLEMENTACIÓN DE getRol()
    // ========================================================
    @Override
    public String getRol() {
        return "Doctor";
    }

    // ========================================================
    // toString() SOBRESCRITO
    // ========================================================
    @Override
    public String toString() {
        return super.toString() + " | Especialidad: " + especialidad
                + " | Estado: " + estadoDoctor;
    }

    // ========================================================
    // MÉTODO AUXILIAR PARA COMBOBOX
    // ========================================================
    /**
     * Representación CORTA del doctor para usar en ComboBox.
     * Formato: "D001 - Dr. Roberto Sánchez (Medicina General)"
     * 
     * @return String con código, nombre y especialidad.
     */
    public String getDescripcionCorta() {
        return getCodigo() + " - " + getNombreCompleto()
                + " (" + getEspecialidad().getEtiqueta() + ")";
    }
}

//Nestor Osegueda