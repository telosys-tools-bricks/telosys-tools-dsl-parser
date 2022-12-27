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

/**
 * Domain model general information (name, version, description )  <br>
 * stored in the ".model" properties file
 *
 * @author Laurent Guerin
 */
public class ModelInfo {
	
	public static final String FILE_NAME = "model.yaml" ;
	
	public static final String TITLE       = "title" ;
	public static final String VERSION     = "version" ;  
	public static final String DESCRIPTION = "description" ;
	
    private String modelTitle = "";
    private String modelVersion = ""; 
    private String modelDescription = "";

    /**
     * Default constructor (required for YAML parsing)
     */
    public ModelInfo() {
        super();
        this.modelTitle = "" ;
        this.modelVersion = "" ; 
        this.modelDescription = "" ;
    }

    /**
     * Returns "" if the given string is null, else keep it
     * To avoid null values set by SnakeYaml if no value in the file
     * @param s
     * @return
     */
    private String voidIfNull(String s) {
    	return s != null ? s : "" ;
    }
    
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
        this.modelTitle = voidIfNull(modelTitle) ;
    }

    //-----------------------------------------------------------------------------
    /**
     * Returns the model version
     * @return
     */
    public String getVersion() {
		return this.modelVersion;
	}
    
    /**
     * Set the model version
     * @param modelVersion
     */
    public final void setVersion(String modelVersion) {
    	this.modelVersion = voidIfNull(modelVersion) ;
    }

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
    	this.modelDescription = voidIfNull(modelDescription) ;
    }
    
    //-----------------------------------------------------------------------------
    @Override
    public String toString() {
        return "modelTitle='" + this.modelTitle
        		+ "' modelVersion='" + this.modelVersion 
        		+ "' modelDescription='" + this.modelDescription + "'";
    }

}
