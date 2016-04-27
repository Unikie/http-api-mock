# HTTP API Mock ![Build status](https://circleci.com/gh/Mystes/http-api-mock.svg?style=shield&circle-token=eb4602f147bdd5cf4c64fb363e4f1df3da490313)
This is work based on SOAP/REST Mock Service by Jacek Obrymski (https://sourceforge.net/projects/soaprest-mocker/).
Original features are documented at https://sourceforge.net/p/soaprest-mocker/wiki/Home/.

#### Added features:
* Include XML declarations in recorded requests.
* Get HTTP headers of recorded requests.
* Add custom HTTP header to a custom response
* Support recording resource ids of REST POST requests
* Support recording resource paths of REST requests
* Support recording request parameters of SOAP POST requests
* Support returning binary content in default responses of REST operations
* Support validating XML namespace of SOAP body root element
* Support for specifying default response HTTP headers in configuration file
* Support for returning SOAP with attachment (binary).

## Usage
With HTTP API Mock you can easily mock external (and internal if needed) interfaces. It's a good tool to be used for integration tests and also during development. With HTTP API Mock you can easily mock SOAP interfaces and also JSON/XML REST interfaces.
Basic usage can be read from https://sourceforge.net/p/soaprest-mocker/wiki/Home/.

### Configuration
#### Step 1. Add as Maven dependency
Configuration below uses Jetty as the container. It should be possible to use other containers as well.
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>2.4</version>
    <executions>
        <execution>
            <id>copy-wars</id>
            <phase>package</phase>
            <goals>
                <goal>copy</goal>
            </goals>
            <configuration>
                <outputDirectory>${project.build.directory}/external-wars</outputDirectory>
                <stripVersion>true</stripVersion>
                <artifactItems>
                    <artifactItem>
                        <groupId>fi.mystes</groupId>
                        <artifactId>http-api-mock</artifactId>
                        <version>1.0.0</version>
                        <type>war</type>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
<plugin>
    <groupId>org.mortbay.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>8.1.5.v20120716</version>
    <configuration>
        <stopKey>STOP</stopKey>
        <stopPort>9992</stopPort>
        <scanIntervalSeconds>5</scanIntervalSeconds>
        <webAppConfig>
            <contextPath>/mock</contextPath>
            <extraClasspath>${basedir}/resources</extraClasspath>
        </webAppConfig>
        <war>${project.build.directory}/external-wars/http-api-mock.war</war>
        <connectors>
            <connector implementation="org.eclipse.jetty.server.nio.SelectChannelConnector">
                <port>8888</port>
                <maxIdleTime>60000</maxIdleTime>
            </connector>
        </connectors>
        <useTestClasspath>true</useTestClasspath>
    </configuration>
    <executions>
        <execution>
            <id>start-jetty</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>stop</goal>
                <goal>run</goal>
            </goals>
            <configuration>
                <scanIntervalSeconds>0</scanIntervalSeconds>
                <daemon>true</daemon>
            </configuration>
        </execution>
        <execution>
            <id>stop-jetty</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

You also need to add this part to your pom.xml. Here we define the public maven repository where HTTP API mock will be fetched during build process.
```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-mystes-maven</id>
        <name>bintray</name>
        <url>http://dl.bintray.com/mystes/maven</url>
    </repository>
</repositories>
```

#### Step 2. Configure endpoints
Endpoints are configured in `ws-mock.properties` file under `resources` folder. Original features are documented at https://sourceforge.net/p/soaprest-mocker/wiki/Home/#configuring-mocked-webservice-endpoints.

##### Original features
`ws-mock.properties`:
<table>
<thead>
<tr>
    <td>Property</td>
    <td>Description</td>
</tr>
</thead>
<tbody>
<tr>
    <td>SERVICE[i].NAME</td>
    <td>name of webservice</td>
</tr>
<tr>
    <td>SERVICE[i].TYPE</td>
    <td>SOAP or REST</td>
</tr>
<tr>
    <td>SERVICE[i].WSDL</td>
    <td>name of wsdl file, relevant only to SOAP webservices; the wsdl file should be placed in the classpath visible to the mock service application</td>
</tr>
<tr>
    <td>SERVICE[i].OPERATION[j].INPUT_MESSAGE</td>
    <td>(optional) root element of request message; relevant only to SOAP webservices; skip this if WSDL is provided, this information will be read from the WSDL file</td>
</tr>
<tr>
    <td>SERVICE[i].OPERATION[j].HTTP_METHOD</td>
    <td>HTTP method of mocked REST service; relevant only to REST webservices; available methods: GET, POST, PUT, DELETE</td>
</tr>
<tr>
    <td>SERVICE[i].OPERATION[j].DEFAULT_RESPONSE</td>
    <td>name of file containing default response; the file should be placed on classpath; skip this if WSDL is provided, the default response will be generated from the WSDL file</td>
</tr>
<tr>
    <td>SERVICE[i].OPERATION[j].DEFAULT_RESPONSE_CODE</td>
    <td>(optional, only for REST, default: 200) HTTP code of default response</td>
</tr>
<tr>
    <td>SERVICE[i].OPERATION[j].DEFAULT_RESPONSE_CONTENT_TYPE</td>
    <td>(optional, only for REST, default: text/xml ) content type of default response, e.g. application/json, text/xml</td>
</tr>
</tbody>
</table>

You can add multiple webservice endpoint definitions. Just add next SERVICE[i] definition with incremented [i] index.
Within particular SOAP WS endpoint you can define multiple operations and within REST service definition you can service different HTTP methods.
Just add next OPERATION[j] definition incrementing [j] index.

##### Added features
<table>
<thead>
<tr>
    <td>Feature</td>
    <td>How to use it</td>
</tr>
</thead>
<tbody>
<tr>
    <td>Include xml declarations to mock server recorded requests</td>
    <td><code>SERVICE[0].IGNORE_XML_DECLARATION=false</code></td>
</tr>
<tr>
    <td>Get the HTTP headers of recorded requests</td>
    <td>Send GET request to url:<br />
     http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/recorded-request-headers</td>
</tr>
<tr>
    <td>Add custom HTTP header to a custom response</td>
    <td>http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/responses?headers=yourHeaderName1:yourHeaderValue1,yourHeaderName2:yourHeaderValue2</td>
</tr>
<tr>
    <td>Support recording resource paths of REST requests </td>
    <td>Similar to recording resource ids, but complex paths are supported. By default mocked REST services support recording resource ids (see above) but in some cases the mock requests will contain more than one id (or a path postfix that is dynamic for some other reason). In these cases you can enable recording resource paths instead of just resource ids. <br /> Resource path support can be enabled for a REST service in ws-mock.properties: <br /><code>SERVICE[i].ENABLE_RESOURCE_PATHS=true</code><br />Resource paths of requests can be obtained by sending GET request to URL: <br /> http://server:port/{APP_CONTEXT}/services/REST/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/recorded-resource-paths.<br /> Resource path support is available for all HTTP methods in supported in soaprest-mock (GET,PUT,POST,DELETE).</td>
</tr>
<tr>
    <td>Support recording request parameters of SOAP POST requests</td>
    <td> Send GET request to url:<br /> http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/recorded-request-params</td>
</tr>
<tr>
    <td>Support returning binary content in default responses of REST operations</td>
    <td>ws-mock.properties example:<br /><br />
    <pre>
    <code>
    SERVICE[7].NAME=dummy-rest-binary
    SERVICE[7].TYPE=REST
    SERVICE[7].OPERATION[0].HTTP_METHOD=GET
    SERVICE[7].OPERATION[0].BINARY=true
    SERVICE[7].OPERATION[0].DEFAULT_RESPONSE=default_rest_get_binary_response.gif
    SERVICE[7].OPERATION[0].DEFAULT_RESPONSE_CONTENT_TYPE=application/octet-stream
    </code>
    </pre>
    <br /><br />The BINARY property of an operation forces the default response to return a binary file specified in DEFAULT_RESPONSE using content type set in DEFAULT_RESPONSE_CONTENT_TYPE</td>
</tr>
<tr>
    <td>Support specifying default response HTTP headers in configuration file</td>
    <td>ws-mock.properties example:<br /><br />
    <pre>
    <code>
    SERVICE[8].NAME=dummy-rest-for-headers
    SERVICE[8].TYPE=REST
    SERVICE[8].OPERATION[0].HTTP_METHOD=GET
    SERVICE[8].OPERATION[0].DEFAULT_RESPONSE=dummy_default_rest_get_response.xml
    SERVICE[8].OPERATION[0].DEFAULT_RESPONSE_CODE=200
    SERVICE[8].OPERATION[0].DEFAULT_RESPONSE_CONTENT_TYPE=text/xml
    SERVICE[8].OPERATION[0].DEFAULT_RESPONSE_HEADERS=Header1:Value1,Header-2:header_value_2
    </code></pre></td>
</tr>
<tr>
    <td>Support for sending SOAP with attachment</td>
    <td>ws-mock.properties example <br /><br />
    <pre><code>
    SERVICE[9].NAME=dummy-soap-binary
    SERVICE[9].TYPE=SOAP
    SERVICE[9].OPERATION[0].BINARY=true
    SERVICE[9].OPERATION[0].INPUT_MESSAGE=dummySoapRequest
    SERVICE[9].OPERATION[0].DEFAULT_RESPONSE=default_soap_multipart_response.dat
    SERVICE[9].OPERATION[0].DEFAULT_RESPONSE_CONTENT_TYPE=multipart/related;type="application/xop+xml";start="http://tempuri.org/0";boundary="boundary123123";start-info="application/soap+xml"
    </code>
    </pre>
    <br />
    The response must be hand crafted to contain boundaries, XML part and binary part. See `src/test/resources/it/default_soap_multipart_response.dat` for example.
    </td>
</tr>
</tbody>

</table>

#### Step 3. Start HTTP API Mock

If you are using Maven and Maven configuration above, you can manually start the HTTP API Mock service in your project by running:
```bash
mvn jetty:deploy-war
```
Manually running the service is useful when developing code or integration tests using SoapUI client.

#### Step 4. Use HTTP API Mock -services
Mock services can be invoked and controlled by REST API.

| Action | URL | Notes |
| --- | --- | --- |
| List all available services | GET `http://server:port/{APP_CONTEXT}/services` |  |
| Addresses of mocked services | GET `http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/endpoint` | This will list all configured endpoints. |
| Initialize | POST `http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/init` | Initializes the endpoint and clears previously recorded requests and manually set responses. |
| Set response | POST `http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/responses` | Response will be served on next request to endpoint. Multiple requests to this URL will set consecutive responses. |
| Set n'th response | `http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/responses/{n}` |  |
| Get recorded requests | GET `http://server:port/{APP_CONTEXT}/services/{SERVICE[i].TYPE}/{SERVICE[i].NAME}/operations/{SERVICE[i].OPERATION[j].INPUT_MESSAGE}/recorded-requests` | Returns all requests since last call to initialize. |

### Run with your integration tests.
The maven configuration above starts the http-api-mock at `pre-integration-test` phase and stops it at `post-integration-test` phase. So the configuration assumes that you have defined integration tests to be run at `integration-test` phase.
Integration tests can be implemented for example with SoapUI (https://www.soapui.org/). SoapUI test can be run in maven with `soapui-maven-plugin` (https://github.com/SmartBear/soapui).

#### SoapUI examples

If you need some examples how to use HTTP API Mock with SoapUI, you can clone this git-project and open the example SoapUI-file `SoapRest-mock-soapui-project.xml` under `SoapUI` folder. To be able to run the test cases you must start the HTTP Api Mock service (in the cloned project):

```bash
mvn jetty:deploy-war
```

These example API's are configured here: `src/test/resources/it/ws-mock.properties`.

## Developing
Fork, develop, create a pull request.
Remember to add some tests!


## [License](LICENSE)
GNU LESSER GENERAL PUBLIC LICENSE version 3
