package org.telosys.tools.dsl.generic.model;

import java.util.ArrayList;
import java.util.List;

import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Link;

public class GenericEntity implements Entity {
	
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private String databaseCatalog = "";
	private List<ForeignKey> databaseForeignKeys = new ArrayList<ForeignKey>();
	private String databaseSchema = "";
	private String databaseTable = "";
	private String databaseType = "";
	private String fullName = "";
	private List<Link> links = new ArrayList<Link>();
	private String name = "";
	private String _package = "";
	private Boolean tableType = Boolean.FALSE;
	private Boolean viewType = Boolean.FALSE;

	public Attribute getAttributeByName(String name) {
		for(Attribute attribute : getAttributes()) {
			if(name.equals(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public String getDatabaseCatalog() {
		return databaseCatalog;
	}
	public void setDatabaseCatalog(String databaseCatalog) {
		this.databaseCatalog = databaseCatalog;
	}
	public List<ForeignKey> getDatabaseForeignKeys() {
		return databaseForeignKeys;
	}
	public void setDatabaseForeignKeys(List<ForeignKey> databaseForeignKeys) {
		this.databaseForeignKeys = databaseForeignKeys;
	}
	public String getDatabaseSchema() {
		return databaseSchema;
	}
	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}
	public String getDatabaseTable() {
		return databaseTable;
	}
	public void setDatabaseTable(String databaseTable) {
		this.databaseTable = databaseTable;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackage() {
		return _package;
	}
	public void setPackage(String _package) {
		this._package = _package;
	}
	public Boolean isTableType() {
		return tableType;
	}
	public void setTableType(Boolean tableType) {
		this.tableType = tableType;
	}
	public Boolean isViewType() {
		return viewType;
	}
	public void setViewType(Boolean viewType) {
		this.viewType = viewType;
	}
	
}
