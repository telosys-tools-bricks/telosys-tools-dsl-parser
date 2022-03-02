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
package org.telosys.tools.dsl.model;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyColumn;

public class DslModelForeignKey_OLD implements ForeignKey {
	
    private final String fkName; 
    private final String tableName; // table holding this FK
    private final String referencedTableName; // table referenced by this FK
    
    private final List<ForeignKeyColumn> columns;
    
    private final String deferrable  = "" ;
    private final int deferrableCode = 0 ;
    private final String deleteRule = "";
    private final int deleteRuleCode = 0;
    private final String updateRule = "";
    private final int updateRuleCode = 0;

    
    public DslModelForeignKey_OLD(String fkName, String tableName, String referencedTableName) {
		super();
        this.fkName = fkName;
        this.tableName = tableName;
        this.referencedTableName = referencedTableName;
        this.columns = new LinkedList<>();
	}

	@Override
    public String getName() {
        return fkName;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

    @Override
    public String getTableName() {
        return tableName;
    }

//    public void setTableName(String tableName) {
//        this.tableName = tableName;
//    }

    @Override
    public String getReferencedTableName() {
        return referencedTableName;
    }

//    public void setReferencedTableName(String referencedTableName) {
//        this.referencedTableName = referencedTableName;
//    }

    @Override
    public List<ForeignKeyColumn> getColumns() {
        return columns;
    }

    public void addColumn(ForeignKeyColumn fkCol) {
        this.columns.add(fkCol);
    }

    @Override
    public String getDeferrable() {
        return deferrable;
    }

//    public void setDeferrable(String deferrable) {
//        this.deferrable = deferrable;
//    }

    @Override
    public int getDeferrableCode() {
        return deferrableCode;
    }

//    public void setDeferrableCode(int deferrableCode) {
//        this.deferrableCode = deferrableCode;
//    }

    @Override
    public String getDeleteRule() {
        return deleteRule;
    }

//    public void setDeleteRule(String deleteRule) {
//        this.deleteRule = deleteRule;
//    }

    @Override
    public int getDeleteRuleCode() {
        return deleteRuleCode;
    }

//    public void setDeleteRuleCode(int deleteRuleCode) {
//        this.deleteRuleCode = deleteRuleCode;
//    }

    @Override
    public String getUpdateRule() {
        return updateRule;
    }

//    public void setUpdateRule(String updateRule) {
//        this.updateRule = updateRule;
//    }

    @Override
    public int getUpdateRuleCode() {
        return updateRuleCode;
    }

//    public void setUpdateRuleCode(int updateRuleCode) {
//        this.updateRuleCode = updateRuleCode;
//    }
}
