package org.telosys.tools.dsl.main;

import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.generic.model.Model;

public class Main {

    public static void main(String[] args) {

        // check argument exist
        if (args == null) {
            throw new EntityParserException("No file given");
        }

        if (args.length != 1) {
            throw new EntityParserException("A single parameter is required");
        }

        String dslFolder = args[0];

        Application application = new Application();
        Model model = application.parseAndConvert(dslFolder);
    }

}
