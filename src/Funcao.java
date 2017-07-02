
public enum Funcao {
	MEDIA(1, "Média"),
	MEDIANA(2, "Mediana"),
	MMQ(3, "Método dos mínimos quadrados");
	
	private int codigo;
	private String displayName;
	
	Funcao(int codigo, String displayName){
		this.codigo = codigo;
	}
	
	int getCodigo() {
		return this.codigo;
	}
	
	@Override public String toString() { return this.displayName; }
}
