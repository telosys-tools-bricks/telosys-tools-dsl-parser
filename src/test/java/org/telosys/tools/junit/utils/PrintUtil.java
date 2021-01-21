package org.telosys.tools.junit.utils;

import java.util.List;

import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.DslModelManager;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyColumn;

public class PrintUtil {

	public static void printErrors(DslModelManager dslModelManager) {
		System.out.println("DslModelManager errors : ");
		System.out.println(" Error message : " + dslModelManager.getErrorMessage());
		DslModelErrors errors = dslModelManager.getErrors();
		if (errors != null) {
			System.out.println(" All Errors Count : " + errors.getAllErrorsCount());
			System.out.println(" All Errors : ");
			for (String err : errors.getAllErrorsList()) {
				System.out.println(" . " + err);
			}
			System.out.println(" Errors for each entity : ");
			for (String entityName : errors.getEntities()) {
				System.out.println(" --> Entity '" + entityName + "' : ");
				for (String err : errors.getErrors(entityName)) {
					System.out.println(" . " + err);
				}
			}
		}
	}
	
	public static void printForeignKeys(Entity entity) {
    	System.out.println("Foreign Keys for entity '" + entity.getClassName() + "' : " );
        List<ForeignKey> fkList = entity.getDatabaseForeignKeys();
        for ( ForeignKey fk : fkList ) {
        	System.out.println(" . '" + fk.getName()  + "' : table '" + fk.getTableName() + "' --> table '" + fk.getReferencedTableName() +"'" );
            for ( ForeignKeyColumn fkCol : fk.getColumns() ) {
            	System.out.println("   - col '" + fkCol.getColumnName() + "' --> col '" + fkCol.getReferencedColumnName() + "'" );
            }
        }
    }	
}
