package org.entrega4;

public class Videojuego {
    private int id;
    private String nombre;
    private String estudio;
    private int anio;

    public Videojuego(int id, String nombre, String estudio, int anio) {
        this.id = id;
        this.nombre = nombre;
        this.estudio = estudio;
        this.anio = anio;
    }

    public Videojuego() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstudio() {
        return estudio;
    }

    public void setEstudio(String estudio) {
        this.estudio = estudio;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    @Override
    public String toString() {
        return "Videojuego{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estudio='" + estudio + '\'' +
                ", anio=" + anio +
                '}';
    }
}
