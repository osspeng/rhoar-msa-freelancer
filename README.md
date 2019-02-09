Freelancer4J - An MSA example using RHOAR frameworks
===

---
How to deploy the application to OpenShift
---
1. Create a project in an OpenShift cluster, for example
```
oc new-project freelancer4j --display-name="Freelancer4J Project" --description="A sample project for Freelancer4J"
```
1. deploy the `Freelancer` service, following the README in `Freelancer` project
    * **NOTICE:** In order to provide the test bed for the integration test in API Gateway project, please make sure follow the instruction in README create correct numbers and payloads of test data.
1. deploy the `Project` service, following the README in `Project` project
    * **NOTICE:** In order to provide the test bed for the integration test in API Gateway project, please make sure follow the instruction in README create correct numbers and payloads of test data.
1. deploy the `API-Gateway` service, following the README in `API-Gateway` project

### After deploying all required services to OpenShift, you make make http request using following URI with respective host path.

* Querying Freelancers data using following uri:
  * `/gateway/freelancers/`
  * `/gateway/freelancers/1`
* Querying Projects by getting uri `/projects/`
  * `/gateway/projects/`
  * `/gateway/projects/111111`
  * `/gateway/projects/status/OPEN` 