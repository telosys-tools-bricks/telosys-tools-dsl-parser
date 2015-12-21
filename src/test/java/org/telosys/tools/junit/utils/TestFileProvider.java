package org.telosys.tools.junit.utils;

import java.io.File;

import org.telosys.tools.commons.FileUtil;

public class TestFileProvider {
	
	private final static String SRC_TEST_RESOURCES   = "src/test/resources/" ;
	private final static String TARGET_TESTS_TMP_DIR = "target/tests-tmp/" ;


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
	
	public final static File getTargetTmpFile(String fileName) {
		return new File(TARGET_TESTS_TMP_DIR + fileName);
	}
	
	public final static File copyAndGetTargetTmpFile(String fileName) {
		String srcFileName  = SRC_TEST_RESOURCES + fileName ;
		String destFileName = TARGET_TESTS_TMP_DIR + fileName ;
		
		try {
			FileUtil.copy(srcFileName, destFileName, true);
		} catch (Exception e) {
			throw new RuntimeException("TEST ENV : Cannot copy file '" + fileName + "' in target", e);
		}
		
		File newFile = new File(destFileName);
		if ( ! newFile.exists() ) {
			throw new RuntimeException("TEST ENV : Expected file doesn't exist '" + fileName + "' ");
		}
		return newFile;
	}
	
	public final static void removeTargetTmpFileIfExists(String fileName) {
		String fullFileName = TARGET_TESTS_TMP_DIR + fileName ;		
		File file = new File(fullFileName);
		if ( file.exists() ) {
			if ( ! file.delete() ) {
				throw new RuntimeException("TEST ENV : Cannot delete file '" + fileName + "' ");
			}
		}
	}
}
