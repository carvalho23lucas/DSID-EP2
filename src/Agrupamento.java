
public enum Agrupamento{
		ANO(3, "Ano"),MES(2, "Mês"),
		DIA(1, "Dia da semana");
		
		private int codigo;
		private String displayName;

		
		Agrupamento(int codigo, String displayName){
			this.codigo = codigo;
			this.displayName = displayName;
		}
		
		int getCodigo() {
			return this.codigo;
		}
		
		@Override public String toString() { return displayName; }
	}
