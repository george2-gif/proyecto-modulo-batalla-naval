package batallanaval;

public class Coordenada {
	 private int fila;
	    private int columna;

	    public Coordenada(int fila, int columna) {
	        this.fila = fila;
	        this.columna = columna;
	    }

	    public int getFila() {
	        return fila;
	    }

	    public void setFila(int fila) {
	        this.fila = fila;
	    }

	    public int getColumna() {
	        return columna;
	    }

	    public void setColumna(int columna) {
	        this.columna = columna;
	    }

	   
	    public boolean equals(Object obj) {
	        if (this == obj) return true;
	        if (obj == null || getClass() != obj.getClass()) return false;
	        Coordenada that = (Coordenada) obj;
	        return fila == that.fila && columna == that.columna;
	    }

	    
	    public int hashCode() {
	        return 31 * fila + columna;
	    }

	    
	    public String toString() {
	        char letraFila = (char) ('A' + fila);
	        return letraFila + String.valueOf(columna + 1);
	    }
	}


