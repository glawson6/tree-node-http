# tree-node-http
### Data Focused Java Developer - Programming Test
### Cloud Service Programming Exercise – Tree Manager
 
Design and implement a RESTful web service which manages an arbitrarily structured tree or hierarchy of nodes.

A node in the tree should have the following mandatory properties:
+ A globally unique name
+ Brief description.Any additional properties on a node are up to the developer. 
+ A node in the tree can have a parent node, and can have 0 to 15 children (an attempt to add more than 15 child nodes to a parent will result in an error).  The tree managed by the service will have a single, parent node, named ‘root’, that will always exist and can never be modified or deleted.

Functional Requirements:

The service will provide resources to support the following:
+ A dd a node to the tree at a specific location (for instance, add a new node to a leaf node’s children)
+ Retrieve a single node
+ Retrieve the immediate children of a node
+ Retrieve all descendants of a node (immediate children and nested children)
+ For an arbitrary node, retrieve all ancestors/parents of the node (the path from the root node to the specific node).
+ Remove a node from the tree (also removes all of its children)

Non-Functional Requirements:
+ The sample project must include instructions for building and running the project.  Preferably, projects should use a build tool such as Maven, Gradle, Make, etc.
+ The service should handle any error conditions (such as invalid input or internal errors) with suitable HTTP error responses. 
+ The developer is responsible for designing the API signatures, including the input/output data structures, and any exceptions deemed necessary.

Although Java is preferred, the choice of language and frameworks is at the discretion of the developer.  Ideally, the application will run as a simple process/executable, and not require an external container or web server to run.
Projects can be submitted to us either via a zip/tarball containing all source, or alternatively, a link to an available GitHub, Bitbucket, or similar repository.
 
 
### To run this app

 ./mvnw clean package -Dmaven.test.skip=true && java -jar target/tree-node-http.jar 
 
 If you want to quickly interact with tthe app install [httpie](https://httpie.org/#installation). Start the application in on terminal. Open 
 another terminal and navigate from the project root to src/test/resources/utils. On Linux/Cygwin systems, you should be able to run
  ./createSomeNodes.sh to create some nodes.

 
### API test functionality

### NOTE The root node ID is ROOT.

If you ever need to know the paths of leaves on the tree. The sample below is generated from running ./createSomeNodes.sh script.

```
$ http -v GET localhost:8080/api/treenode/paths
GET /api/treenode/paths HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:8080
User-Agent: HTTPie/0.9.3



HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Fri, 09 Dec 2016 16:16:09 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "CH1": "ROOT", 
    "CH2": "ROOT", 
    "CH3": "ROOT", 
    "CH4": "ROOT.CH2", 
    "CH5": "ROOT.CH2.CH4", 
    "CH6": "ROOT.CH2", 
    "CH7": "ROOT.CH1", 
    "ROOT": ""
}

```

Add a node to the tree at a specific location (for instance, add a new node to a leaf node’s children)

```
http -v POST localhost:8080/api/treenode < testNode1.json 
POST /api/treenode HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 105
Content-Type: application/json
Host: localhost:8080
User-Agent: HTTPie/0.9.3

{
    "parentNodeId": "ROOT", 
    "treeNode": {
        "description": "The TESTNODE1", 
        "id": "TESTNODE1"
    }
}

HTTP/1.1 201 Created
Content-Type: application/json;charset=UTF-8
Date: Fri, 09 Dec 2016 15:48:54 GMT
Location: http://localhost:8080/api/treenode/TESTNODE1
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "parentNodeId": "ROOT", 
    "treeNode": {
        "description": "The TESTNODE1", 
        "id": "TESTNODE1"
    }
}

```

Retrieve a single node

```
$ http -v GET localhost:8080/api/treenode/CH3
GET /api/treenode/CH3 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:8080
User-Agent: HTTPie/0.9.3



HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Fri, 09 Dec 2016 15:59:08 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "description": "The node at row3", 
    "id": "CH3"
}

```

Retrieve the immediate children of a node

```
$ http -v GET localhost:8080/api/treenode/children/CH2
GET /api/treenode/children/CH2 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:8080
User-Agent: HTTPie/0.9.3



HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Fri, 09 Dec 2016 16:03:16 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "family": {
        "children": {
            "CH4": {
                "description": "The node4", 
                "id": "CH4"
            }, 
            "CH6": {
                "description": "The node6", 
                "id": "CH6"
            }
        }
    }
}

```

Retrieve all descendants of a node (immediate children and nested children)

```
$ http -v GET localhost:8080/api/treenode/descendants/CH2
GET /api/treenode/descendants/CH2 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:8080
User-Agent: HTTPie/0.9.3



HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Fri, 09 Dec 2016 16:04:54 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "family": {
        "descendants": {
            "CH4": {
                "description": "The node4", 
                "id": "CH4"
            }, 
            "CH5": {
                "description": "The node5", 
                "id": "CH5"
            }, 
            "CH6": {
                "description": "The node6", 
                "id": "CH6"
            }
        }
    }
}

```

For an arbitrary node, retrieve all ancestors/parents of the node (the path from the root node to the specific node).

```
$ http -v GET localhost:8080/api/treenode/ancestors/CH4
GET /api/treenode/ancestors/CH4 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:8080
User-Agent: HTTPie/0.9.3



HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Fri, 09 Dec 2016 18:10:31 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "family": {
        "ancestors": [
            {
                "description": "The node4", 
                "id": "CH4"
            }, 
            {
                "description": "The node at row2", 
                "id": "CH2"
            }, 
            {
                "description": "This is the root node", 
                "id": "ROOT"
            }
        ]
    }
}


```

Remove a node from the tree (also removes all of its children)

```
$ http -v DELETE localhost:8080/api/treenode/CH2
DELETE /api/treenode/CH2 HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 0
Host: localhost:8080
User-Agent: HTTPie/0.9.3



HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Fri, 09 Dec 2016 16:58:47 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked

{
    "msg": "Node at CH2 deleted.", 
    "success": true
}

```

### Links

[httpie](https://httpie.org/#installation)

[Spring Boot](https://github.com/spring-projects/spring-boot)

[Maven Wrapper](https://github.com/takari/maven-wrapper)

