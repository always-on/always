package edu.wpi.always.cm;

public class SchemaConfig {
	private final Class<? extends Schema> type;
	private final long updateDelay;

	public SchemaConfig(Class<? extends Schema> type, long updateDelay){
		this.type = type;
		this.updateDelay = updateDelay;
	}

	public Class<? extends Schema> getType() {
		return type;
	}

	public long getUpdateDelay() {
		return updateDelay;
	}
	
}
