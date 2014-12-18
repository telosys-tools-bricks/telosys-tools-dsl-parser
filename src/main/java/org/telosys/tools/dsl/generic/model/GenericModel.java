package org.telosys.tools.dsl.generic.model;

import java.util.ArrayList;
import java.util.List;

import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.ModelType;

public class GenericModel implements Model {
	
	private String name = "";
	private String version = "";
	private String description = "";
	private ModelType type;
	private Integer databaseId = Integer.valueOf(0);
	private String databaseProductName = "";
	private List<Entity> entities = new ArrayList<Entity>();

	public Entity getEntityByName(String name) {
		for(Entity entity : getEntities()) {
			if(name.equals(entity.getName())) {
				return entity;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ModelType getType() {
		return type;
	}
	public void setType(ModelType type) {
		this.type = type;
	}
	public Integer getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(Integer databaseId) {
		this.databaseId = databaseId;
	}
	public String getDatabaseProductName() {
		return databaseProductName;
	}
	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
}
