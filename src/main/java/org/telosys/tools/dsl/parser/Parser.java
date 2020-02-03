package org.telosys.tools.dsl.parser;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.telosys.tools.commons.PropertiesManager;
import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainTag;

public class Parser {

    private static final String DOT_MODEL           = ".model"  ;

//	private final List<String> entitiesNames;
	
    /*
	 * Entities files with errors 
	 * Key   : entity absolute file name 
	 * Value : parsing error
	 */
	private final Hashtable<String,String> entitiesErrors = new Hashtable<>();
	
    public Parser() {
		super();
//		this.entitiesNames = getEntitiesNames();
	}

    public Hashtable<String,String> getErrors() {
    	return entitiesErrors ;
    }
    
    /**
     * Parse the given model file
     *
     * @param file the ".model" file 
     * @return
     */
    public final DomainModel parseModel(File file) {
    	checkModelFile(file);
    	return parseModelFile(file);
    }
    
    protected static void checkModelFile(File file) {
        if ( ! file.exists() ) {
            String textError = "File '" + file.toString() + "' not found";
            throw new DslParserException(textError);
        }
        if ( ! file.isFile() ) {
            String textError = "'" + file.toString() + "' is not a file";
            throw new DslParserException(textError);
        }
        if ( ! file.getName().endsWith(DOT_MODEL)) {
            String textError = "File '" + file.toString() + "' doesn't end with '" + DOT_MODEL + "'";
            throw new DslParserException(textError);
        }
    }


//    private List<String> getEntitiesNames() {
//    	List<String> list = new LinkedList<>();
//    	list.add("Car");  // TODO : get entities names from model files 
//    	return list;
//    }
    
    /**
     * Parse the given MODEL file
     *
     * @param file the model file ( file with ".model" suffix )
     * @return
     */
    private final DomainModel parseModelFile(File file) {
    	
    	//checkModelFile(file);
    	
    	//--- Step 1 : load model information
        PropertiesManager propertiesManager = new PropertiesManager(file);
        Properties properties = propertiesManager.load();
        
        DomainModel model = new DomainModel(properties);
        
        List<String> entitiesFileNames = DslModelUtil.getEntitiesAbsoluteFileNames(file);
        List<String> entitiesNames = new LinkedList<>();
        
        //--- Step 2 : build void entities (1 instance for each entity defined in the model)
        for (String entityFileName : entitiesFileNames) {
            String entityName = DslModelUtil.getEntityName(new File(entityFileName));
            entitiesNames.add(entityName);
            model.addEntity(new DomainEntity(entityName));
        }

        //--- Step 3 : parse each entity and populate it in the model
        int errorsCount = 0 ;
        for (String entityFileName : entitiesFileNames) {
        	//--- Parse
        	DomainEntity domainEntity;
			try {
//				domainEntity = entityParser.parse(entityFileName);
				domainEntity = parseEntity(entityFileName, entitiesNames);
	        	//--- Populate
	            model.populateEntityFileds(domainEntity.getName(), domainEntity.getFields() );
			} catch (DslParserException parsingException) {
				errorsCount++ ;
				File entityFile = new File(entityFileName);
				entitiesErrors.put(entityFile.getName(), parsingException.getMessage() );
			}
        }
        if ( errorsCount == 0 ) {
            return model;
        }
        else {
        	throw new DslParserException( "Parsing error(s) : " + errorsCount + " invalid entity(ies) ") ;
        }
    }
    
    /**
     * Parse the given ENTITY file name
     * @param entityFileName
     * @return
     */
    protected final DomainEntity parseEntity(String entityFileName, List<String> entitiesNames) {
    	File entityFile = new File(entityFileName);
    	return parseEntity(entityFile, entitiesNames);
    }
    
    /**
     * Parse the given ENTITY file 
     * @param file
     * @return
     */
    protected final DomainEntity parseEntity(File file, List<String> entitiesNames) {
    	
    	EntityFileParser entityFileParser = new EntityFileParser(file);
    	EntityFileParsingResult result = entityFileParser.parse();
    	String entityNameFromFileName = result.getEntityNameFromFileName();
    	ParserLogger.log("\n----------");
    	DomainEntity domainEntity = new DomainEntity(entityNameFromFileName);
    	for ( FieldParts field : result.getFields() ) {
//    		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(field);
    		DomainField domainField = parseField(entityNameFromFileName, field, entitiesNames); 
        	domainEntity.addField(domainField);
    	}
    	return domainEntity;
    }
    
    /**
     * Parse the given RAW FIELD 
     * @param entityNameFromFileName
     * @param field
     */
    protected final DomainField parseField(String entityNameFromFileName, FieldParts field, List<String> entitiesNames) {
    	
    	// 1) Parse the field NAME and TYPE
		FieldNameAndTypeParser parser = new FieldNameAndTypeParser(entityNameFromFileName, entitiesNames);
		FieldNameAndType fieldNameAndType = parser.parseFieldNameAndType(field);
		ParserLogger.log("Field : name '" + fieldNameAndType.getName() 
			+ "' type '" + fieldNameAndType.getType() 
			+ "' cardinality = " + fieldNameAndType.getCardinality() );
		
		String fieldName = fieldNameAndType.getName();
		
		//--- New "DomainField" instance
		DomainField domainField = new DomainField(fieldName, 
				fieldNameAndType.getDomainType(), fieldNameAndType.getCardinality());
		
		// 2) Parse field ANNOTATIONS and TAGS
		FieldAnnotationsAndTagsParser fieldAnnotationsAndTagsParser = new FieldAnnotationsAndTagsParser(entityNameFromFileName);
		FieldAnnotationsAndTags fieldAnnotationsAndTags = fieldAnnotationsAndTagsParser.parse(fieldName, field);
//		List<DomainAnnotationOrTag> annotationsAndTagsList = fieldAnnotationsAndTags.getAnnotations();
//		System.out.println("\n--- ANNOTATIONS and TAGS : " + annotationsAndTagsList.size() );
//		for ( DomainAnnotationOrTag annotationOrTag : annotationsAndTagsList ) {
//			if ( annotationOrTag instanceof DomainTag ) {
//				System.out.println(" . TAG : " + annotationOrTag );
//				//--- Add a new TAG 
//				domainField.addTag((DomainTag)annotationOrTag);
//			}
//			else if ( annotationOrTag instanceof DomainAnnotation ) {
//				System.out.println(" . ANNOTATION : " + annotationOrTag );
//				//--- Add a new ANNOTATION 
//				domainField.addAnnotation((DomainAnnotation)annotationOrTag);
//			}
//		}
		
		
		// Errors found
		for ( AnnotationOrTagError error : fieldAnnotationsAndTags.getErrors() ) {
			domainField.addError(error);
		}
		
		// Annotations found
		for ( DomainAnnotation annotation : fieldAnnotationsAndTags.getAnnotations() ) {
			if ( domainField.hasAnnotation(annotation) ) {
				// Already defined => Error
				domainField.addError(
						new AnnotationOrTagError(entityNameFromFileName, fieldName, 
						annotation.getName(), "annotation defined more than once" )  );
			}
			else {
				domainField.addAnnotation(annotation);
			}
		}
		
		// Tags found
		for ( DomainTag tag : fieldAnnotationsAndTags.getTags() ) {
			if ( domainField.hasTag(tag) ) {
				// Already defined => Error
				domainField.addError(
						new AnnotationOrTagError(entityNameFromFileName, fieldName, 
						tag.getName(), "tag defined more than once" )  );
			}
			else {
				domainField.addTag(tag);
			}
		}
		
		ParserLogger.log("--- ");
		return domainField;
    }
}
