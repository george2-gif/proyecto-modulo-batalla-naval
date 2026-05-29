package batallanaval;

import java.util.ArrayList;
import java.util.List;

public class Barco {

    private String nombre;
    private int tamano;
    private List<Coordenada> posiciones;
    private boolean[] impactos;

    public Barco(String nombre, int tamano) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.posiciones = new ArrayList<>();
        this.impactos = new boolean[tamano];
    }

    public String getNombre() {
        return nombre;
    }

    public int getTamano() {
        return tamano;
    }

    public List<Coordenada> getPosiciones() {
        return posiciones;
    }

    public void setPosiciones(List<Coordenada> posiciones) {
        this.posiciones = posiciones;
    }

    public boolean contieneCoordenada(Coordenada coord) {
        return posiciones.contains(coord);
    }

    public boolean recibirImpacto(Coordenada coord) {
        int indice = indiceDePosicion(coord);
        if (indice == -1) return false;
        if (impactos[indice]) return false;
        impactos[indice] = true;
        return true;
    }

    private int indiceDePosicion(Coordenada coord) {
        for (int i = 0; i < posiciones.size(); i++) {
            if (posiciones.get(i).equals(coord)) return i;
        }
        return -1;
    }

    public boolean estaHundido() {
        for (boolean impacto : impactos) {
            if (!impacto) return false;
        }
        return true;
    }

    public int contarImpactos() {
        int contador = 0;
        for (boolean impacto : impactos) {
            if (impacto) contador++;
        }
        return contador;
    }
}
