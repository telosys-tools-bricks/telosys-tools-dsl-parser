package org.telosys.tools.junit.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TestFileProvider {
	
	public static File getFileByClassPath(String fileName) {
		URL url = TestFileProvider.class.getResource(fileName);
		if ( url != null ) {
			URI uri = null ;
			try {
				uri = url.toURI();
			} catch (URISyntaxException e) {
				throw new RuntimeException("Cannot convert URL to URI (file '" + fileName + "')");
			}
			return new File(uri);
		}
		else {
			throw new RuntimeException("File '" + fileName + "' not found");
		}
//		return new File(url.toURI());
	}

}
