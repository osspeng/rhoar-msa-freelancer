package com.freelance4J.project.service;


import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.freelance4J.project.service.impl.ProjectServiceImpl;
import com.freelance4J.project.service.model.Project;
import com.freelance4J.project.service.model.Project.Status;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ProjectServiceTest extends MongoTestBase {

    private Vertx vertx;

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        JsonObject config = getConfig();
        mongoClient = MongoClient.createNonShared(vertx, config);
        Async async = context.async();
        dropCollection(mongoClient, "projects", async, context);
        async.await(10000);
    }

    @After
    public void tearDown() throws Exception {
        mongoClient.close();
        vertx.close();
    }

    @Test
    public void testAddProject(TestContext context) throws Exception {
        String projectId = "999999";
        String firstName = "Jason";
        String lastName = "Peng";
        String email = "hpeng@redhat.com";
        String title = "Project Pheonix";
        Project project = new Project();
        project.setProjectId(projectId);
        project.setFirstName(firstName);
        project.setLastName(lastName);
        project.setEmail(email);
        project.setTitle(title);
        project.setStatus(Status.OPEN);

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.addProject(project, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                JsonObject query = new JsonObject().put("_id", projectId);
                mongoClient.findOne("projects", query, null, ar1 -> {
                    if (ar1.failed()) {
                        context.fail(ar1.cause().getMessage());
                    } else {
                        assertThat(ar1.result().getString("firstName"), equalTo(firstName));
                        assertThat(ar1.result().getString("lastName"), equalTo(lastName));
                        async.complete();
                    }
                });
            }
        });
    }

    @Test
    public void testGetProjects(TestContext context) throws Exception {    	
    	Async saveAsync = context.async(2);
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("status", Project.Status.OPEN.toString());

        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        String projectId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("firstName", "Phil")
                .put("lastName", "Andrew")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project AngryBird")
                .put("status", Project.Status.INPROGRESS.toString());

        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProjects(ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().size(), equalTo(2));
                Set<String> itemIds = ar.result().stream().map(p -> p.getProjectId()).collect(Collectors.toSet());
                assertThat(itemIds.size(), equalTo(2));
                assertThat(itemIds, allOf(hasItem(projectId1),hasItem(projectId2)));
                async.complete();
            }
        });
    }

    @Test
    public void testGetProject(TestContext context) throws Exception {
        Async saveAsync = context.async(2);
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("status", Project.Status.OPEN.toString());

        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        String projectId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("firstName", "Phil")
                .put("lastName", "Andrew")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project AngryBird")
                .put("status", Project.Status.INPROGRESS.toString());
        
        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProject("111111", ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().getProjectId(), equalTo("111111"));
                assertThat(ar.result().getFirstName(), equalTo("Jason"));
                async.complete();
            }
        });
    }

    @Test
    public void testGetNonExistingProject(TestContext context) throws Exception {
        Async saveAsync = context.async(1);
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("status", Project.Status.OPEN.toString());
        
        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProject("222222", ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), nullValue());
                async.complete();
            }
        });
    }

    @Test
    public void testPing(TestContext context) throws Exception {
    	ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();
        service.ping(ar -> {
            assertThat(ar.succeeded(), equalTo(true));
            async.complete();
        });
    }
    
    
    @Test
    public void testGetProjectsByStatus(TestContext context) throws Exception {    	
    	Async saveAsync = context.async(2);
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("status", Project.Status.OPEN.toString());

        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        String projectId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("firstName", "Phil")
                .put("lastName", "Andrew")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project AngryBird")
                .put("status", Project.Status.INPROGRESS.toString());

        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        saveAsync.await();

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProjectsByStatus(Project.Status.OPEN, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().size(), equalTo(1));
                Set<String> itemIds = ar.result().stream().map(p -> p.getProjectId()).collect(Collectors.toSet());
                assertThat(itemIds.size(), equalTo(1));
                assertThat(itemIds,hasItem(projectId1));
                async.complete();
            }
        });
    }

}
