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

public abstract class DomainAnnotationOrTag {

    private final String  name;
    private final String  stringParameter;
    private final Number  numberParameter;
    private final Boolean booleanParameter;
    private final boolean hasParameter;

    /**
     * Constructor for annotation without parameter
     * @param name
     */
    protected DomainAnnotationOrTag(String name) {
        this.name = name;
        this.stringParameter  = null;
        this.numberParameter  = null;
        this.booleanParameter = null;
        this.hasParameter = false;
    }

    /**
     * Constructor for annotation with string parameter
     * @param name
     * @param stringParameter
     */
    protected DomainAnnotationOrTag(String name, String stringParameter) {
        this.name = name;
        this.stringParameter  = stringParameter;
        this.numberParameter  = null;
        this.booleanParameter = null;
        this.hasParameter = true;
    }

    /**
     * Constructor for annotation with number parameter
     * @param name
     * @param numberParameter
     */
    protected DomainAnnotationOrTag(String name, Number numberParameter) {
        this.name = name;
        this.stringParameter  = null;
        this.numberParameter  = numberParameter;
        this.booleanParameter = null;
        this.hasParameter = true;
    }

    /**
     * Constructor for annotation with boolean parameter
     * @param name
     * @param booleanParameter
     */
    protected DomainAnnotationOrTag(String name, Boolean booleanParameter) {
        this.name = name;
        this.stringParameter  = null;
        this.numberParameter  = null;
        this.booleanParameter = booleanParameter;
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
    	if ( numberParameter instanceof BigDecimal ) { // null is not an instanceof anything
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
    	if ( numberParameter instanceof Integer ) { // null is not an instanceof anything
            return (Integer) numberParameter;
    	}
    	return null ;
    }

    /**
     * Returns the annotation boolean parameter <br>
     * or null if none
     * @return
     */
    public Boolean getParameterAsBoolean() {
        return booleanParameter;
    }

    public Object getParameter() {
    	if ( this.hasParameter ) {
            if (numberParameter != null ) {
            	return numberParameter;
            }
            else if (stringParameter != null ) {
            	return stringParameter;
            }
            else if (booleanParameter != null ) {
            	return booleanParameter;
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
        	if ( stringParameter != null ) {
            	sb.append(stringParameter);
        	}
        	if ( numberParameter != null ) {
            	sb.append(numberParameter);
        	}
        	if ( booleanParameter != null ) {
            	sb.append(booleanParameter);
        	}
        	sb.append(")");
    	}
    	return sb.toString();
    }
}
