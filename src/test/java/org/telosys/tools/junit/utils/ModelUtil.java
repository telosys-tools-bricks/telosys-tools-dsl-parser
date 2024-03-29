package org.telosys.tools.junit.utils;

import java.io.File;

import org.telosys.tools.dsl.DslModelManager;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.ParserV2;
import org.telosys.tools.dsl.parser.ParsingResult;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.generic.model.Model;

public class ModelUtil {

	private ModelUtil() {} 
	
	public static void println(String s) {
		System.out.println(s);
	}

	private static ParsingResult parseModel(File modelFolder) {
        return (new ParserV2()).parseModel(modelFolder);
	}
//	private static ParsingResult parseModel(String modelFolderName) {
//        ParserV2 parser = new ParserV2();
//        return parser.parseModel(modelFolderName);
//	}
	
	public static DomainModel parseValidModel(String modelFolderName) {
		return parseValidModel(new File(modelFolderName));
	}
	public static DomainModel parseValidModel(File modelFolder) {
		ParsingResult result = parseModel(modelFolder);
		if ( result.getErrors().getNumberOfErrors() > 0 ) {
			PrintUtil.printErrors(result.getErrors());
			throw new RuntimeException("Cannot load model : error(s) in model");
		}
		else if ( result.getModel() == null ) {
			throw new RuntimeException("Cannot load model : model is null");
		}
		else {
			return result.getModel();
		}
	}
	
	public static DslModelErrors parseInvalidModel(String modelFolderName) {
		return parseInvalidModel(new File(modelFolderName));
	}
	public static DslModelErrors parseInvalidModel(File modelFolder) {
		ParsingResult result = parseModel(modelFolder);
		if ( result.getErrors().getNumberOfErrors() == 0 ) {
			throw new RuntimeException("No error (errors expected)");
		}
//		else if ( result.getModel() != null ) {
//			throw new RuntimeException("Model not null (null expected)");
//		}
		else {
			DslModelErrors errors = result.getErrors();
			PrintUtil.printErrors(errors);
			return errors;
		}
	}
	
	
	public static Model loadValidModel(String modelFileName) {
		println("----- " );
		println("Loading valid model : " + modelFileName );
        DslModelManager dslModelManager = new DslModelManager();
        Model model = dslModelManager.loadModel(modelFileName);
        PrintUtil.printErrors(dslModelManager.getErrors());
		if ( model == null || dslModelManager.getErrors().getNumberOfErrors() > 0 ) {
			println("ERROR : cannot load model");
			throw new RuntimeException("Cannot load model : " + dslModelManager.getErrorMessage());
		}
		return model ;
    }

}
