package batallanaval;

public class Barco {
	 private String name;
	    private int size;
	    private int hits;
	    private boolean horizontal;
	    private int startRow;
	    private int startCol;

	    public Barco(String name, int size) {
	        this.name = name;
	        this.size = size;
	        this.hits = 0;
	        this.horizontal = true;
	        this.startRow = -1;
	        this.startCol = -1;
	    }

	    public String getName() { return name; }
	    public int getSize() { return size; }
	    public int getHits() { return hits; }
	    public boolean isHorizontal() { return horizontal; }
	    public int getStartRow() { return startRow; }
	    public int getStartCol() { return startCol; }

	    public void setHorizontal(boolean horizontal) { this.horizontal = horizontal; }
	    public void setStartRow(int startRow) { this.startRow = startRow; }
	    public void setStartCol(int startCol) { this.startCol = startCol; }

	    
	    public boolean hit() {
	        hits++;
	        return isSunk();
	    }

	    
	    public boolean isSunk() {
	        return hits >= size;
	    }

	    
	    public String toString() {
	        return name + " (" + size + " espacios)";
	    }
	}
	    
	


