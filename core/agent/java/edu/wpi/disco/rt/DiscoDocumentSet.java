package edu.wpi.disco.rt;

import java.util.*;

import org.w3c.dom.*;

/***
 * A simple data class that holds a disco task model's xml Document and related
 * properties.
 * Properties can be null.
 * @author Bahador
 * 
 */
public class DiscoDocumentSet {
	private final Document mainDocument;
	private final Properties properties;
	private final Properties translateProperties;

	public DiscoDocumentSet(Document main, Properties properties,
			Properties translateProperties) {
		this.mainDocument = main;
		this.properties = properties;
		this.translateProperties = translateProperties;
	}

	public DiscoDocumentSet(Document main, Properties properties) {
		this(main, properties, null);
	}

	public DiscoDocumentSet(Document main) {
		this(main, null, null);
	}

	public Document getMainDocument() {
		return mainDocument;
	}

	public Properties getProperties() {
		return properties;
	}

	public Properties getTranslateProperties() {
		return translateProperties;
	}
}
