
public enum Variavel {
	TEMP("Temperatura"),
	DEWP("DEWP"),
	SLP("SLP"),
	STP("STP"),
	VISIB("Visibilidade"),
	WDSP("WDSP");
	
	private String displayName;
	
	Variavel(String displayName){
		this.displayName = displayName;
	}
	
	@Override public String toString() { return displayName;}
}
