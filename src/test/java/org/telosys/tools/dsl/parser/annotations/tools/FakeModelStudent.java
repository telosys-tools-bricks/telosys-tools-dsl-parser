package org.telosys.tools.dsl.parser.annotations.tools;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;

public class FakeModelStudent {
	
	private FakeModelStudent() {}
	
	//------------------------------------------------------------------------
	// Build fake model
	//------------------------------------------------------------------------

	public static DslModel buildModel() {
		DslModel model = new DslModel("MyModel");
		model.addEntity(buildStudentEntity());
		model.addEntity(buildTeacherEntity());
		return model;
	}
	private static DslModelEntity buildStudentEntity() {
		DslModelEntity e = new DslModelEntity("Student");
		e.addAttribute(new DslModelAttribute("id", "int"));
		e.addAttribute(new DslModelAttribute("lastName", "string"));
		e.addLink(new DslModelLink("teacher")); // Owning side link
		return e;
	}
	private static DslModelEntity buildTeacherEntity() {
		DslModelEntity e = new DslModelEntity("Teacher");
		e.addAttribute(new DslModelAttribute("code", "int"));
		e.addAttribute(new DslModelAttribute("lastName", "string"));
		e.addLink(new DslModelLink("students")); // Inverse side link with MappedBy('teacher' link)
		return e;
	}
}
