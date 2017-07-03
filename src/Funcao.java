
public enum Funcao {
	MEDIA(1, "Média"),
	DESVIOPADRAO(2, "Desvio Padrão"),
	MMQ(3, "Método mín. quadrados");
	
	private int codigo;
	private String displayName;
	
	Funcao(int codigo, String displayName){
		this.codigo = codigo;
		this.displayName = displayName;
	}
	
	int getCodigo() {
		return this.codigo;
	}
	
	@Override public String toString() { return displayName; }
}
