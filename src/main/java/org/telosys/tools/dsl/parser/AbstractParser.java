package org.telosys.tools.dsl.parser;

import org.slf4j.Logger;
import org.telosys.tools.dsl.EntityParserException;

public abstract class AbstractParser {

    private final Logger logger;
    

	public AbstractParser(Logger logger) {
		super();
		this.logger = logger;
	}

	protected void logInfo(String message) {
		logger.info(message);
	}
	
	protected void throwParsingError(String entityName, String message) {
        String errorMessage = entityName + " : " + message ;
        this.logger.error(errorMessage);
        throw new EntityParserException(errorMessage);
	}
}
