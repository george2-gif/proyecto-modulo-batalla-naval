package batallanaval;

public abstract class Jugador {
	
	 protected String nombre;
	    protected Tablero tablero;

	    public Jugador(String nombre) {
	        this.nombre = nombre;
	        this.tablero = new Tablero();
	    }

	    public String getNombre() {
	        return nombre;
	    }

	    public Tablero getTablero() {
	        return tablero;
	    }

	    public abstract void colocarBarcos();

	    public abstract Coordenada obtenerDisparo();

	    public boolean haPerdido() {
	        return tablero.todosHundidos();
	    }

	    public void mostrarTableroPropio() {
	        tablero.mostrarConLeyenda(true, nombre);
	    }

	    public void mostrarTableroOponente(Jugador oponente) {
	        oponente.getTablero().mostrarConLeyenda(false, oponente.getNombre());
	    }
	}


