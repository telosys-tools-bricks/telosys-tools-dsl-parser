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

import java.util.Properties;

/**
 * Domain model general information (name, version, description )  <br>
 * stored in the ".model" properties file
 *
 * @author Laurent Guerin
 */
public class DomainModelInfo {

	//private static final String NAME        = "name" ; // v 3.3.0
	private static final String TITLE       = "title" ; // v 3.3.0
	//private static final String VERSION     = "version" ;  
	private static final String DESCRIPTION = "description" ;
	
    // private String modelName = ""; // v 3.3.0
    private String modelTitle = ""; // v 3.3.0
    //private String modelVersion = ""; // v 3.3.0
    private String modelDescription = "";

    /**
     * Default constructor
     */
    public DomainModelInfo() {
        super();
        // this.modelName = "" ; // v 3.3.0
        this.modelTitle = "" ; // v 3.3.0
        // this.modelVersion = "" ; // v 3.3.0
        this.modelDescription = "" ;
    }

    /**
     * Constructor
     *
     * @param properties
     */
    public DomainModelInfo(Properties properties) {
        super();
        if ( properties != null ) {
            //this.modelName = properties.getProperty(NAME, ""); // v 3.3.0
            this.modelTitle = properties.getProperty(TITLE, ""); // v 3.3.0
            //this.modelVersion = properties.getProperty(VERSION, ""); // v 3.3.0
            this.modelDescription = properties.getProperty(DESCRIPTION, "");
        }
    }

    /**
     * Returns the set of properties representing the current model information
     * @return
     */
    public Properties getProperties() {
    	Properties properties = new Properties();
    	//properties.put(NAME, modelName); // v 3.3.0
    	properties.put(TITLE, modelTitle); // v 3.3.0
    	//properties.put(VERSION, modelVersion); // v 3.3.0
    	properties.put(DESCRIPTION, modelDescription);
    	return properties ;
    }
    
//    //-----------------------------------------------------------------------------
//    /**
//     * Returns the model name
//     *
//     * @return
//     */
//    public final String getName() {
//        return modelName;
//    }
//
//    /**
//     * Set the model name
//     * @param modelName
//     */
//    public final void setName(String modelName) {
//        this.modelName = modelName ;
//    }
//
    //-----------------------------------------------------------------------------
    /**
     * Returns the model title
     *
     * @return
     */
    public final String getTitle() {
        return modelTitle;
    }

    /**
     * Set the model title
     * @param modelTitle
     */
    public final void setTitle(String modelTitle) {
        this.modelTitle = modelTitle ;
    }

//    //-----------------------------------------------------------------------------
//    /**
//     * Returns the model version
//     * @return
//     */
//    public String getVersion() {
//		return modelVersion;
//	}
//    
//    /**
//     * Set the model version
//     * @param modelVersion
//     */
//    public final void setVersion(String modelVersion) {
//    	this.modelVersion = modelVersion ;
//    }
//
    //-----------------------------------------------------------------------------
	/**
     * Returns the model description
	 * @return
	 */
	public String getDescription() {
		return modelDescription;
	}
	
    /**
     * Set the model description
     * @param modelDescription
     */
    public final void setDescription(String modelDescription) {
    	this.modelDescription = modelDescription ;
    }
    
    //-----------------------------------------------------------------------------
    @Override
    public String toString() {
//        return "modelName='" + this.modelName 
//        		+ "' modelVersion='" + this.modelVersion 
//        		+ "' modelDescription='" + this.modelDescription + "'";
        return "modelTitle='" + this.modelTitle
        		+ "' modelDescription='" + this.modelDescription + "'";
    }

}
