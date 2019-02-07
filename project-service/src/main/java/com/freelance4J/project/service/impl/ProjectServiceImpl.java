package com.freelance4J.project.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.freelance4J.project.service.ProjectService;
import com.freelance4J.project.service.model.Project;
import com.freelance4J.project.service.model.Project.Status;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class ProjectServiceImpl implements ProjectService {

    private MongoClient client;

    public ProjectServiceImpl(Vertx vertx, JsonObject config, MongoClient client) {
        this.client = client;
    }

    @Override
    public void getProjects(Handler<AsyncResult<List<Project>>> resulthandler) {
        JsonObject query = new JsonObject();
        client.find("projects", query, ar -> {
            if (ar.succeeded()) {
                List<Project> projects = ar.result().stream()
                                           .map(json -> new Project(json))
                                           .collect(Collectors.toList());
                resulthandler.handle(Future.succeededFuture(projects));
            } else {
                resulthandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void getProject(String projectId, Handler<AsyncResult<Project>> resulthandler) {
        JsonObject query = new JsonObject().put("projectId", projectId);
        client.find("projects", query, ar -> {
            if (ar.succeeded()) {
                Optional<JsonObject> result = ar.result().stream().findFirst();
                if (result.isPresent()) {
                    resulthandler.handle(Future.succeededFuture(new Project(result.get())));
                } else {
                    resulthandler.handle(Future.succeededFuture(null));
                }
            } else {
                resulthandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void addProject(Project project, Handler<AsyncResult<String>> resulthandler) {
        client.save("projects", toDocument(project), resulthandler);
    }

    @Override
    public void ping(Handler<AsyncResult<String>> resultHandler) {
        resultHandler.handle(Future.succeededFuture("OK"));
    }

    private JsonObject toDocument(Project project) {
        JsonObject document = project.toJson();
        document.put("_id", project.getProjectId());
        return document;
    }

	@Override
	public void getProjectsByStatus(Status status, Handler<AsyncResult<List<Project>>> resulthandler) {
		JsonObject query = new JsonObject().put("status", status.toString());
        System.out.println("PARAM: "+query.encodePrettily());
        client.find("projects", query, ar -> {
            if (ar.succeeded()) {
                List<Project> projects = ar.result().stream()
                                           .map(json -> new Project(json))
                                           .collect(Collectors.toList());
                for (JsonObject json : ar.result()) {
                    System.out.println("RESULT: "+json.encodePrettily());
                  }
                resulthandler.handle(Future.succeededFuture(projects));
            } else {
                resulthandler.handle(Future.failedFuture(ar.cause()));
            }
        });
		
	}

}
