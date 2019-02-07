package com.freelance4J.project.service.model;

import java.io.Serializable;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Project implements Serializable {

    private static final long serialVersionUID = -6994655395272795259L;

    private String projectId;
    private String firstName;
    private String lastName;
    private String email;
    private String title;
    private String description;

    private Status status;
    
	public enum Status {
//		OPEN("0"), INPROGRESS("1"), COMPLETED("2"), CANCELLED("3");
		OPEN, INPROGRESS, COMPLETED, CANCELLED;
		private String value;
		
		Status() {
		}
		
		Status(String value) {
			this.value = value;
		}
		
//		@Override
//		public String toString() {
//			return this.value;
//		}
    }
   

    public Project() {

    }

    public Project(JsonObject json) {
    	this.projectId = json.getString("projectId");
    	this.firstName = json.getString("firstName");
    	this.lastName = json.getString("lastName");
    	this.email = json.getString("email");
    	this.title = json.getString("title");
    	this.description = json.getString("description");
    	this.status = Status.valueOf(json.getString("status"));
    }

   

    public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.put("projectId", this.projectId);
        json.put("firstName", this.firstName);
        json.put("lastName", this.lastName);
        json.put("email", this.email);
        json.put("title", this.title);
        json.put("description", this.description);
        json.put("status", this.status.toString());
        return json;
    }

}
