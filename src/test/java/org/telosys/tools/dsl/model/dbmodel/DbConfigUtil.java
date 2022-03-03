package org.telosys.tools.dsl.model.dbmodel;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DatabasesConfigurations;
import org.telosys.tools.commons.dbcfg.DbConfigManager;

public class DbConfigUtil {
	
	public DatabaseConfiguration getDatabaseConfigurations(String dbcfgFile, int dbId) throws TelosysToolsException {
		DbConfigManager dbConfigManager = new DbConfigManager( FileUtil.getFileByClassPath(dbcfgFile) );
		DatabasesConfigurations databasesConfigurations = dbConfigManager.load();
		return databasesConfigurations.getDatabaseConfiguration(dbId);
	}
	
}
