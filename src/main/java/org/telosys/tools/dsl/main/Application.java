package org.telosys.tools.dsl.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.generic.converter.Converter;
import org.telosys.tools.dsl.parser.DomainModelParser;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.generic.model.Model;

import java.io.File;

public class Application {

    static Logger logger = LoggerFactory.getLogger(Main.class);

    public Model parseAndConvert(String dslFolder) {
        // call parser tool
        DomainModelParser domainModelParser = new DomainModelParser();
        logger.info("\nParse folder : "+dslFolder);
        DomainModel domainModel = domainModelParser.parse(new File(dslFolder));
        logger.info("\n"+domainModel.toString());

        Converter converter = new Converter();
        Model model = converter.convertToGenericModel(domainModel);
        logger.info(model.toString());

        return model;
    }

}
