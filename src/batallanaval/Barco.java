package batallanaval;

public class Barco {

	public static final String PORTAAVIONES = "Portaaviones";
	public static final String ACORAZADO = "Acorazado";
	public static final String CRUCERO = "Crucero";
	public static final String SUBMARINO = "Submarino";
	public static final String DESTRUCTOR = "Destructor";

	private String nombre;
	private int tamanio;
	private int impactos;
	private boolean hundido;
	private int[] filasOcupadas;
	private int[] columnasOcupadas;
	private int direccionMovimiento; 

	public Barco(String nombre, int tamanio) {
		this.nombre = nombre;
		this.tamanio = tamanio;
		this.impactos = 0;
		this.hundido = false;
		this.filasOcupadas = new int[tamanio];
		this.columnasOcupadas = new int[tamanio];
		this.direccionMovimiento = 1;
	}

	public boolean recibirImpacto() {
		impactos++;
		if (impactos >= tamanio)
			hundido = true;
		return hundido;
	}

	public boolean ocupaCelda(int fila, int columna) {
		for (int i = 0; i < tamanio; i++)
			if (filasOcupadas[i] == fila && columnasOcupadas[i] == columna)
				return true;
		return false;
	}

	public void colocarEn(int[] filas, int[] columnas) {
		this.filasOcupadas = filas.clone();
		this.columnasOcupadas = columnas.clone();
	}

	public String getSimbolo() {
		switch (nombre) {
		case PORTAAVIONES:
			return "P";
		case ACORAZADO:
			return "A";
		case CRUCERO:
			return "C";
		case SUBMARINO:
			return "S";
		case DESTRUCTOR:
			return "D";
		default:
			return "B";
		}
	}


	public String getNombre() {
		return nombre;
	}

	public int getTamanio() {
		return tamanio;
	}

	public int getImpactos() {
		return impactos;
	}

	public boolean isHundido() {
		return hundido;
	}

	public int[] getFilasOcupadas() {
		return filasOcupadas;
	}

	public int[] getColumnasOcupadas() {
		return columnasOcupadas;
	}

	public int getDireccionMovimiento() {
		return direccionMovimiento;
	}

	public void setDireccionMovimiento(int d) {
		this.direccionMovimiento = d;
	}

	@Override
	public String toString() {
		return nombre + " (tamaño " + tamanio + ") HP: " + (tamanio - impactos) + "/" + tamanio;
	}
}