/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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

import java.util.Properties;

/**
 * Domain model general information (name, version, description )  <br>
 * stored in the ".model" properties file
 *
 * @author Laurent Guerin
 */
public class DomainModelInfo {

    private final String modelName;
    private final String modelVersion;
    private final String modelDescription;

    /**
     * Constructor
     *
     * @param properties
     */
    public DomainModelInfo(Properties properties) {
        super();
        this.modelName = properties.getProperty("name", "");
        this.modelVersion = properties.getProperty("version", "");
        this.modelDescription = properties.getProperty("description", "");
    }

    /**
     * Default constructor
     */
    public DomainModelInfo() {
        super();
        this.modelName = "" ;
        this.modelVersion = "" ;
        this.modelDescription = "" ;
    }

    public Properties getProperties() {
    	// TODO
    	return null ;
    }
    
    /**
     * Returns the model name
     *
     * @return
     */
    public final String getName() {
        return modelName;
    }

    /**
     * Returns the model version
     * @return
     */
    public String getVersion() {
		return modelVersion;
	}

	/**
     * Returns the model description
	 * @return
	 */
	public String getDescription() {
		return modelDescription;
	}

    @Override
    public String toString() {
        return "modelName='" + this.modelName 
        		+ "' modelVersion='" + this.modelVersion 
        		+ "' modelDescription='" + this.modelDescription + "'";
    }

}
