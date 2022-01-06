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
package org.telosys.tools.dsl;

import java.util.LinkedList;
import java.util.List;

/**
 * Model Parsing : errors collector
 * 
 * @author Laurent GUERIN
 *
 */
public class DslModelErrors {

	private final List<DslModelError> errors ;

    /**
     * Constructor with a single error message
     * @param errorMessage
     */
    public DslModelErrors() {
        super();
        this.errors = new LinkedList<>();
    }

    /**
     * Returns a list containing all errors
     * @return
     */
    public List<DslModelError> getErrors() {
    	return errors ;
    }
    
    /**
     * Returns the number of errors
     * @return
     */
    public int getNumberOfErrors() {
        return errors.size();
    }

    /**
     * Returns true if there's no error
     * @return
     */
    public boolean isEmpty() {
        return errors.isEmpty();
    }

    /**
     * Add a new error in the collector
     * @param error
     */
    public void addError(DslModelError error) {
    	errors.add(error);
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(errors.size());
    	sb.append(" error(s) : \n");
		for ( DslModelError e : errors ) {
	    	sb.append(" . " );
	    	sb.append(e.getReportMessage() );
	    	sb.append("\n");
		}
		return sb.toString();
    }
}
