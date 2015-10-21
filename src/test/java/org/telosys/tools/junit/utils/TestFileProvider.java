package org.telosys.tools.junit.utils;

import java.io.File;

public class TestFileProvider {
	
	private final static String SRC_TEST_RESOURCES = "src/test/resources/" ;

//	public static File getFileByClassPath(String fileName) {
//		URL url = TestFileProvider.class.getResource(fileName);
//		if ( url != null ) {
//			URI uri = null ;
//			try {
//				uri = url.toURI();
//			} catch (URISyntaxException e) {
//				throw new RuntimeException("Cannot convert URL to URI (file '" + fileName + "')");
//			}
//			return new File(uri);
//		}
//		else {
//			throw new RuntimeException("File '" + fileName + "' not found");
//		}
////		return new File(url.toURI());
//	}
	
	public final static File getTestFile(String fileName) {
		File file = new File(SRC_TEST_RESOURCES + fileName);
		if ( file.exists() ) {
			if ( file.isFile() ) {
				return file ;
			}
			else {
				throw new RuntimeException("Test resource file '"+ file.getName() + "' is not a file");
			}
		}
		else {
			throw new RuntimeException("Test resource file '"+ file.getName() + "' not found");
		}
	}

}
