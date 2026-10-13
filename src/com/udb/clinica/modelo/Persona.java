// ============================================================
// PAQUETE: com.udb.clinica.modelo
// CLASE:   Persona (abstracta)
//
// ¿QUÉ HACE?
// Representa a cualquier persona registrada en la clínica.
// Es la superclase de Paciente y Doctor.
//
// ¿POR QUÉ ES ABSTRACTA?
// Porque no existen "personas" genéricas en la clínica.
// Solo existen Pacientes y Doctores. La clase sirve como molde.
//
// HERENCIA:
//   Paciente extends Persona
//   Doctor   extends Persona
// ============================================================

package com.udb.clinica.modelo;

/**
 * Superclase abstracta Persona.
 * 
 * Contiene los atributos y métodos COMUNES a Paciente y Doctor.
 */
public abstract class Persona {

    // ========================================================
    // ATRIBUTOS (encapsulados: privados)
    // ========================================================
    // 'private' significa que SOLO los métodos de esta clase
    // pueden leerlos/escribirlos. Nadie de afuera directamente.
    // 
    // Esto es ENCAPSULAMIENTO (uno de los pilares de POO).
    // ========================================================
    private String codigo;
    private String nombreCompleto;
    private String telefono;
    private String correo;

    // ========================================================
    // CONSTRUCTOR VACÍO
    // ========================================================
    // ¿Por qué tener un constructor vacío?
    // Porque a veces queremos crear el objeto primero y luego
    // ir llenando los datos con setters.
    // Ejemplo: Persona p = new Paciente(); p.setNombre(...);
    // ========================================================
    public Persona() {
        // No hace nada, pero existe por conveniencia.
    }

    // ========================================================
    // CONSTRUCTOR COMPLETO
    // ========================================================
    // Recibe los 4 atributos comunes.
    // 'this.codigo' se refiere al atributo de la clase,
    // mientras que 'codigo' (sin this) es el parámetro.
    // 
    // Esta distinción es necesaria cuando el parámetro tiene
    // el mismo nombre que el atributo.
    // ========================================================
    public Persona(String codigo, String nombreCompleto, String telefono, String correo) {
        this.codigo = codigo;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correo = correo;
    }

    // ========================================================
    // GETTERS Y SETTERS (encapsulamiento)
    // ========================================================
    // Getters: leen el valor de un atributo privado.
    // Setters: modifican el valor de un atributo privado.
    // 
    // ¿Por qué no hacer públicos los atributos y ya?
    // Porque así podríamos agregar validaciones en los setters
    // (ej. edad > 0) sin romper el código que ya los usa.
    // ========================================================

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // ========================================================
    // MÉTODO ABSTRACTO getRol()
    // ========================================================
    // 'abstract' significa: "no tengo implementación aquí.
    // Cada subclase DEBE proporcionar la suya."
    // 
    // ¿Por qué?
    // Porque "Persona" no tiene un rol específico. Un Paciente
    // es "Paciente" y un Doctor es "Doctor". Esta clase solo
    // declara que EXISTE un método llamado getRol().
    // 
    // ⚠️ IMPORTANTE: si una clase tiene al menos un método
    // abstracto, la clase DEBE ser declarada 'abstract'.
    // ========================================================
    public abstract String getRol();

    // ========================================================
    // toString()
    // ========================================================
    // Devuelve una representación legible del objeto.
    // 
    // ¿Por qué usar String.format()?
    // Porque es más limpio que concatenar con +.
    // 
    // Las subclases harán super.toString() para llamar a este
    // método y luego agregar sus propios datos.
    // 
    // getRol() aquí es POLIMORFISMO: cada subclase devolverá
    // "Paciente" o "Doctor" según corresponda.
    // ========================================================
    @Override
    public String toString() {
        return String.format("%s [%s] - %s | Tel: %s | Email: %s",
                getRol(), codigo, nombreCompleto, telefono, correo);
    }
}
