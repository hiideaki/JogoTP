class Guizos extends Posicao {
	private boolean existe;

	Guizos(int x, int y) {
		super(x, y);
		existe = true;
	}

	public void setExiste(boolean existe) {
		this.existe = existe;
	}

	public boolean isExiste() {
		return existe;
	}

}
