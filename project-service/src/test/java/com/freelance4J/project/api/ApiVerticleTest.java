package com.freelance4J.project.api;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.freelance4J.project.service.ProjectService;
import com.freelance4J.project.service.model.Project;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {

    private Vertx vertx;
    private Integer port;
    private ProjectService projectService;

    /**
     * Before executing our test, let's deploy our verticle.
     * <p/>
     * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
     * completed its start sequence (thanks to `context.asyncAssertSuccess`).
     *
     * @param context the test context.
     */
    @Before
    public void setUp(TestContext context) throws IOException {
      vertx = Vertx.vertx();

      // Register the context exception handler
      vertx.exceptionHandler(context.exceptionHandler());

      // Let's configure the verticle to listen on the 'test' port (randomly picked).
      // We create deployment options and set the _configuration_ json object:
      ServerSocket socket = new ServerSocket(0);
      port = socket.getLocalPort();
      socket.close();

      DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("project.http.port", port));

      //Mock the project Service
      projectService = mock(ProjectService.class);

      // We pass the options as the second parameter of the deployVerticle method.
      vertx.deployVerticle(new ApiVerticle(projectService), options, context.asyncAssertSuccess());
    }

    /**
     * This method, called after our test, just cleanup everything by closing
     * the vert.x instance
     *
     * @param context
     *            the test context
     */
    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetProjects(TestContext context) throws Exception {
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("description", "This is Project Pheonix")
                .put("status", Project.Status.OPEN.toString());
        String projectId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("firstName", "Phil")
                .put("lastName", "Andrew")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project AngryBird")
                .put("description", "This is Project AngryBird")
                .put("status", Project.Status.INPROGRESS.toString());
        List<Project> projects = new ArrayList<>();
        projects.add(new Project(json1));
        projects.add(new Project(json2));
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<List<Project>>> handler = invocation.getArgument(0);
                handler.handle(Future.succeededFuture(projects));
                return null;
             }
         }).when(projectService).getProjects(any());

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/projects", response -> {
                assertThat(response.statusCode(), equalTo(200));
                assertThat(response.headers().get("Content-type"), equalTo("application/json"));
                response.bodyHandler(body -> {
                    JsonArray json = body.toJsonArray();
                    Set<String> itemIds =  json.stream()
                            .map(j -> new Project((JsonObject)j))
                            .map(p -> p.getProjectId())
                            .collect(Collectors.toSet());
                    assertThat(itemIds.size(), equalTo(2));
                    assertThat(itemIds, allOf(hasItem(projectId1),hasItem(projectId2)));
                    verify(projectService).getProjects(any());
                    async.complete();
                })
                .exceptionHandler(context.exceptionHandler());
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }

    @Test
    public void testGetProject(TestContext context) throws Exception {
        String projectId1 = "111111";
        JsonObject json = new JsonObject()
                .put("projectId", projectId1)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("description", "This is Project Pheonix")
                .put("status", Project.Status.OPEN.toString());
        Project project = new Project(json);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<Project>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(project));
                return null;
             }
         }).when(projectService).getProject(eq("111111"),any());

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/projects/111111", response -> {
                assertThat(response.statusCode(), equalTo(200));
                assertThat(response.headers().get("Content-type"), equalTo("application/json"));
                response.bodyHandler(body -> {
                    JsonObject result = body.toJsonObject();
                    assertThat(result, notNullValue());
                    assertThat(result.containsKey("projectId"), is(true));
                    assertThat(result.getString("projectId"), equalTo("111111"));
                    verify(projectService).getProject(eq("111111"),any());
                    async.complete();
                })
                .exceptionHandler(context.exceptionHandler());
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }
    
    @Test
    public void testGetNonExistingProject(TestContext context) throws Exception {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<Project>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(null));
                return null;
             }
         }).when(projectService).getProject(eq("111111"),any());

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/projects/111111", response -> {
                assertThat(response.statusCode(), equalTo(404));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }

    @Test
    public void testAddProject(TestContext context) throws Exception {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<String>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(null));
                return null;
             }
         }).when(projectService).addProject(any(),any());

        Async async = context.async();
        String projectId = "111111";
        JsonObject json = new JsonObject()
                .put("projectId", projectId)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("description", "This is Project Pheonix")
                .put("status", Project.Status.OPEN.toString());
        String body = json.encodePrettily();
        String length = Integer.toString(body.length());
        vertx.createHttpClient().post(port, "localhost", "/projects")
            .exceptionHandler(context.exceptionHandler())
            .putHeader("Content-type", "application/json")
            .putHeader("Content-length", length)
            .handler(response -> {
                assertThat(response.statusCode(), equalTo(201));
                ArgumentCaptor<Project> argument = ArgumentCaptor.forClass(Project.class);
                verify(projectService).addProject(argument.capture(), any());
                assertThat(argument.getValue().getProjectId(), equalTo(projectId));
                async.complete();
            })
            .write(body)
            .end();
    }

    @Test
    public void testLivenessHealthCheck(TestContext context) {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<String>> handler = invocation.getArgument(0);
                handler.handle(Future.succeededFuture("ok"));
                return null;
             }
        }).when(projectService).ping(any());

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/health/liveness", response -> {
                assertThat(response.statusCode(), equalTo(200));
                response.bodyHandler(body -> {
                    JsonObject json = body.toJsonObject();
                    assertThat(json.toString(), containsString("\"outcome\":\"UP\""));
                    async.complete();
                }).exceptionHandler(context.exceptionHandler());
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }

    @Test
    public void testFailingLivenessHealthCheck(TestContext context) {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<Long>> handler = invocation.getArgument(0);
                handler.handle(Future.failedFuture("error"));
                return null;
             }
         }).when(projectService).ping(any());

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/health/liveness", response -> {
                assertThat(response.statusCode(), equalTo(503));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }

    @Test
    public void testLivenessHealthCheckTimeOut(TestContext context) {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Handler<AsyncResult<String>> handler = invocation.getArgument(0);
                // Simulate long operation to force timeout - default timeout = 1000ms
                vertx.<String>executeBlocking(f -> {
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {}
                    f.complete();
                }, res -> handler.handle(Future.succeededFuture("OK")));
                return null;
            }
        }).when(projectService).ping(any());

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/health/liveness", response -> {
                // HealthCheck Timeout returns a 500 status code
                assertThat(response.statusCode(), equalTo(500));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }

    @Test
    public void testReadinessHealthCheck(TestContext context) {
        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/health/readiness", response -> {
                assertThat(response.statusCode(), equalTo(200));
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
    }
    
    
    @Test
    public void testGetProjectsByStatus(TestContext context) throws Exception {
        String projectId1 = "111111";
        JsonObject json1 = new JsonObject()
                .put("projectId", projectId1)
                .put("firstName", "Jason")
                .put("lastName", "Peng")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project Pheonix")
                .put("description", "This is Project Pheonix")
                .put("status", Project.Status.OPEN.toString());
        String projectId2 = "222222";
        JsonObject json2 = new JsonObject()
                .put("projectId", projectId2)
                .put("firstName", "Phil")
                .put("lastName", "Andrew")
                .put("email", "hpeng@redhat.com")
                .put("title", "Project AngryBird")
                .put("description", "This is Project AngryBird")
                .put("status", Project.Status.INPROGRESS.toString());
        List<Project> openPrj = new ArrayList<>();
        openPrj.add(new Project(json1));
//        projects.add(new Project(json2));
        
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<List<Project>>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(openPrj));
                return null;
             }
         }).when(projectService).getProjectsByStatus(eq(Project.Status.OPEN),any());
        
        List<Project> inprogressPrj = new ArrayList<>();
        inprogressPrj.add(new Project(json2));
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation){
                Handler<AsyncResult<List<Project>>> handler = invocation.getArgument(1);
                handler.handle(Future.succeededFuture(inprogressPrj));
                return null;
             }
         }).when(projectService).getProjectsByStatus(eq(Project.Status.INPROGRESS),any());
        

        Async async = context.async();
        vertx.createHttpClient().get(port, "localhost", "/projects/status/OPEN", response -> {
                assertThat(response.statusCode(), equalTo(200));
                assertThat(response.headers().get("Content-type"), equalTo("application/json"));
                response.bodyHandler(body -> {
                	JsonArray json = body.toJsonArray();
                    Set<String> projectIds =  json.stream()
                            .map(j -> new Project((JsonObject)j))
                            .map(p -> p.getProjectId())
                            .collect(Collectors.toSet());
                    System.out.println("TEST RESULT 1: "+json.encodePrettily());
                    assertThat(projectIds.size(), equalTo(1));
                    assertThat(projectIds, hasItem(projectId1));
                	
                    verify(projectService).getProjectsByStatus(eq(Project.Status.OPEN), any());
                    async.complete();
                })
                .exceptionHandler(context.exceptionHandler());
            })
            .exceptionHandler(context.exceptionHandler())
            .end();
        
        vertx.createHttpClient().get(port, "localhost", "/projects/status/INPROGRESS", response -> {
            assertThat(response.statusCode(), equalTo(200));
            assertThat(response.headers().get("Content-type"), equalTo("application/json"));
            response.bodyHandler(body -> {
            	JsonArray json = body.toJsonArray();
                Set<String> projectIds =  json.stream()
                        .map(j -> new Project((JsonObject)j))
                        .map(p -> p.getProjectId())
                        .collect(Collectors.toSet());
                System.out.println("TEST RESULT 2: "+json.encodePrettily());
                assertThat(projectIds.size(), equalTo(1));
                assertThat(projectIds, hasItem(projectId2));
            	
                verify(projectService).getProjectsByStatus(eq(Project.Status.INPROGRESS), any());
                async.complete();
            })
            .exceptionHandler(context.exceptionHandler());
        })
        .exceptionHandler(context.exceptionHandler())
        .end();
    }

}
