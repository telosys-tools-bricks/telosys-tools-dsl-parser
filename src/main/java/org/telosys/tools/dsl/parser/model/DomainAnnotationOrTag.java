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
package org.telosys.tools.dsl.parser.model;

import java.math.BigDecimal;

public class DomainAnnotationOrTag {

    private final String  name;
    private final String  stringParameter;
    private final Number  numberParameter;
    private final boolean hasParameter;

    /**
     * Constructor for annotation without parameter
     * @param name
     */
    public DomainAnnotationOrTag(String name) {
        this.name = name;
        this.stringParameter = null;
        this.numberParameter = null;
        this.hasParameter = false;
    }

    /**
     * Constructor for annotation with string parameter
     * @param name
     * @param param
     */
    public DomainAnnotationOrTag(String name, String param) {
        this.name = name;
        this.stringParameter = param;
        this.numberParameter = null;
        this.hasParameter = true;
    }

    /**
     * Constructor for annotation with number parameter
     * @param name
     * @param param
     */
    public DomainAnnotationOrTag(String name, Number param) {
        this.name = name;
        this.stringParameter = null;
        this.numberParameter = param;
        this.hasParameter = true;
    }

    /**
     * Returns the annotation name ( without '@' )
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the annotation has a parameter
     * @return
     */
    public boolean hasParameter() {
        return this.hasParameter;
    }

    /**
     * Returns the annotation string parameter <br>
     * or null if none
     * @return
     */
    public String getParameterAsString() {
        return stringParameter;
    }

    /**
     * Returns the annotation decimal parameter <br>
     * or null if none
     * @return
     */
    public BigDecimal getParameterAsBigDecimal() {
    	if ( numberParameter != null && numberParameter instanceof BigDecimal ) {
            return (BigDecimal) numberParameter;
    	}
    	return null ;
    }

    /**
     * Returns the annotation integer parameter <br>
     * or null if none
     * @return
     */
    public Integer getParameterAsInteger() {
    	if ( numberParameter != null && numberParameter instanceof Integer ) {
            return (Integer) numberParameter;
    	}
    	return null ;
    }

    public Object getParameter() {
    	if ( this.hasParameter ) {
            if (numberParameter != null ) {
            	return numberParameter;
            }
            else if (stringParameter != null ) {
            	return stringParameter;
            }
    	}
    	return null ;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(name);
    	if ( this.hasParameter ) {
        	sb.append("(");
        	if ( numberParameter != null ) {
            	sb.append(numberParameter);
        	}
        	else {
            	sb.append(stringParameter);
        	}
        	sb.append(")");
    	}
    	return sb.toString();
    }
}
