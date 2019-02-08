package com.freelance4J.apigateway.api;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.freelance4J.apigateway.model.Freelancer;
import com.freelance4J.apigateway.model.Project;
import com.freelance4J.apigateway.service.FreelancerService;
import com.freelance4J.apigateway.service.ProjectService;

@Path("/gateway")
@RequestScoped
public class GatewayResource {
	
	private Logger log = Logger.getLogger(FreelancerService.class.getName());
	
	@Inject
	private FreelancerService freelancerService;
	@Inject
	private ProjectService projectService;
	
	@GET
	@Path("/freelancers")
	@Produces(MediaType.APPLICATION_JSON)
	public Freelancer[] getFreelancers() {
		log.info("GatewayResource.getFreelancers");
		return freelancerService.getFreelancers();
	}

	@GET
	@Path("/freelancers/{freelancerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Freelancer getFreelancer(@PathParam("freelancerId") Integer freelancerId) {
		log.info("GatewayResource.getFreelancer");
		return freelancerService.getFreelancer(freelancerId);
	}
	
	@GET
	@Path("/projects")
	@Produces(MediaType.APPLICATION_JSON)
	public Project[] getProjects() {
		log.info("GatewayResource.getProjects");
		return projectService.getProjects();
	}

	@GET
	@Path("/projects/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProject(@PathParam("projectId") String projectId) {
		log.info("GatewayResource.getProject");
		return projectService.getProject(projectId);
	}
	
	@GET
	@Path("/projects/status/{theStatus}")
	@Produces(MediaType.APPLICATION_JSON)
	public Project[] getProjectsByStatus(@PathParam("theStatus") String theStatus) {
		log.info("GatewayResource.getProjectsByStatus");
		return projectService.getProjectsByStatus(theStatus);
	}

}
