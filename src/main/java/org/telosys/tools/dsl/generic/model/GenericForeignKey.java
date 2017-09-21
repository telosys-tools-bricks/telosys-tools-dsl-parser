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
package org.telosys.tools.dsl.generic.model;

import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyColumn;

import java.util.List;

public class GenericForeignKey implements ForeignKey {
    private String name;
    private String tableName;
    private String referencedTableName;
    private List<ForeignKeyColumn> columns;
    private String deferrable;
    private int deferrableCode;
    private String deleteRule;
    private int deleteRuleCode;
    private String updateRule;
    private int updateRuleCode;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    @Override
    public List<ForeignKeyColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ForeignKeyColumn> columns) {
        this.columns = columns;
    }

    @Override
    public String getDeferrable() {
        return deferrable;
    }

    public void setDeferrable(String deferrable) {
        this.deferrable = deferrable;
    }

    @Override
    public int getDeferrableCode() {
        return deferrableCode;
    }

    public void setDeferrableCode(int deferrableCode) {
        this.deferrableCode = deferrableCode;
    }

    @Override
    public String getDeleteRule() {
        return deleteRule;
    }

    public void setDeleteRule(String deleteRule) {
        this.deleteRule = deleteRule;
    }

    @Override
    public int getDeleteRuleCode() {
        return deleteRuleCode;
    }

    public void setDeleteRuleCode(int deleteRuleCode) {
        this.deleteRuleCode = deleteRuleCode;
    }

    @Override
    public String getUpdateRule() {
        return updateRule;
    }

    public void setUpdateRule(String updateRule) {
        this.updateRule = updateRule;
    }

    @Override
    public int getUpdateRuleCode() {
        return updateRuleCode;
    }

    public void setUpdateRuleCode(int updateRuleCode) {
        this.updateRuleCode = updateRuleCode;
    }
}
