/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.dsl.commons;

public class StringUtil {

	private StringUtil() {}
	
	public static String quote(String s) {
		if (s == null) {
			return s;
		}
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		for (char c : s.toCharArray() ) {
			if ( c == '"') {
				sb.append('\\');
			}
			sb.append(c);
		}
		sb.append('"');
		return sb.toString();
	}

	public static String unquote(String s) {
		if (s == null) {
			return s;
		}
		if ( s.startsWith("\"") && s.endsWith("\"") ) {
			StringBuilder sb = new StringBuilder();
			char[] characters = s.toCharArray() ;
			for ( int i = 1 ; i < characters.length - 1 ; i++ ) {
				char c = characters[i];
				char next = ( i+1 < characters.length - 1 ) ? characters[i+1] : '\0';
				if ( ( c != '\\' ) || ( c == '\\' && next != '\"' ) ) {
					sb.append(c);
				}
			}
			return sb.toString();
		}
		return s;
	}

}
