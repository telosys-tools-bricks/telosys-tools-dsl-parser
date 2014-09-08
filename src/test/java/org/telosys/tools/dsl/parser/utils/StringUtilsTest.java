package org.telosys.tools.dsl.parser.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class StringUtilsTest {
    @Test
    public void testReadStream() throws Exception {
        File file = new File("src/test/resources/string_utils_tests/Employee.entity");
        InputStream inputStream = new FileInputStream(file);

        String formattedContent = StringUtils.readStream(inputStream);

        String compareTo = "Employee {\n" +
                "\tid : integer {@Id}; // the id\n" +
                "\tfirstName : string ;\n" +
                "\tbirthDate : date ;\n" +
                "}\n";

        Assert.assertEquals(compareTo, formattedContent);
    }
    
    @Test
    public void testReadStreamWithAnEmptyText() throws Exception {
        File file = new File("src/test/resources/string_utils_tests/Empty.entity");
        InputStream inputStream = new FileInputStream(file);

        String formattedContent = StringUtils.readStream(inputStream);

        String compareTo = "";

        Assert.assertEquals(compareTo, formattedContent);
    }
    
    @Test(expected=FileNotFoundException.class)
    public void testReadStreamWithNotFoundFiles() throws Exception {
        File file = new File("src/test/resources/string_utils_tests/NotFound.entity");
        
        InputStream inputStream = new FileInputStream(file);

        StringUtils.readStream(inputStream);
     }
}
