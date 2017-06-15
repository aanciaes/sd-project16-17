<body style="color: rgb(0, 0, 0);" class="webpage" alink="#ee0000" link="#0000ee" vlink="#551a8b">
    <div class="header">
      <h1 style="text-align: center;">Distributed Systems <br>
      </h1>
      <h1 style="text-align: center;">Project - phase 1 <br>
      </h1>
      <h2 style="text-align: center;">Engenharia Informática</h2>
      <h3 style="text-align: center;"> Ano lectivo: 2016/2017, 2º
        Semestre</h3>
    </div>
    <h1>Index</h1>
    <div style="margin-left: 40px;"><span style="font-weight: bold;">Deadline</span>:
      April, 13th, 23h59<br style="font-weight: bold;">
      <span style="font-weight: bold;">Report deadline</span>: April,
      28th,
      during test 1 (or in the DI secretary)<br>
    </div>
    <br>
    <ul>
      <li><a href="#objectivo">Goal</a></li>
      <li><a href="#contexto">Components of the system<br>
        </a></li>
      <li><strong><a href="#base1">Funcionalidades base e opcionais</a></strong></li>
      <li><a href="#ambiente">Environment<br>
        </a></li>
      <li><a href="#report">Written Report<br>
        </a></li>
      <li><a href="#delivery">Delivery<br>
        </a></li>
    </ul>
    <h1><a name="objectivo"></a>Goal</h1>
    <p>The goal of the project is to create a system for indexing and
      searching information about (external) documents.&nbsp; <br>
      A document will consist in the following information: an URL and a
      list
      of keywords. <br>
      The system will allow a client to: <br>
    </p>
    <ul>
      <li>Add documents to be indexed;</li>
      <li>Remove documents from the index;<br>
      </li>
      <li>Search for documents, given a set of keywords.<br>
      </li>
    </ul>
    <h2>Does this service make any sense?</h2>
    <p>Indexing services are useful and used in a number of scenarios.
      For
      example, operating systems like Windows and Mac have indexing
      services
      that help searching for files based on their contents (e.g.
      Windows
      search, Spotlight). Document repositories, in addition to
      providing
      document storage and retrieval, also maintain indices for helping
      searching for information on the documents stored (e.g., Apache
      Solr
      can be used to build such systems... and more complex ones :-). In
      the
      former example, the system you will be building could be used for
      indexing information about files by adding each file to the index
      whenever it has changed - the URL would be the URL of the file in
      the
      filesystem (file://...) and the keywords would be the words
      present in
      the files. A similar approach could be used for the second
      example,
      replacing the notion of file for document. Likewise, you can also
      use
      the system you are building for indexing web pages, or any other
      documents that have an URL and for which you can identify a set of
      keywords.<br>
      <br>
      &nbsp; </p>
    <h1><a name="contexto"></a>Components of the system</h1>
    <p>The system must contain, at least, the following components:<br>
      <br>
      <span style="font-weight: bold;">Rendez-vous Server</span><br>
      The rendez-vous server maintains a list of indexing servers. The
      REST
      interface of this server should be the following:</p>
    <p style="margin-left: 40px; background-color: rgb(255, 255, 204);"><code>@Path("/contacts")<br>
        <b>public</b> <b>interface</b> RendezVousService {<br>
        <br>
        &nbsp;&nbsp;&nbsp; @GET<br>
        &nbsp;&nbsp;&nbsp; @Produces(MediaType.APPLICATION_JSON)<br>
        &nbsp;&nbsp;&nbsp; Endpoint[] endpoints();<br>
        <br>
        &nbsp;&nbsp;&nbsp; @POST<br>
        &nbsp;&nbsp;&nbsp; @Path("/{id}")<br>
        &nbsp;&nbsp;&nbsp; @Consumes(MediaType.APPLICATION_JSON)<br>
        &nbsp;&nbsp;&nbsp; <b>void</b> register( @PathParam("id")
        String id,
        Endpoint endpoint);<br>
        <br>
        &nbsp;&nbsp;&nbsp; @DELETE<br>
        &nbsp;&nbsp;&nbsp; @Path("/{id}")<br>
        &nbsp;&nbsp;&nbsp; <b>void</b> unregister(String id);<br>
        }</code></p>
    <p><br>
      <span style="font-weight: bold;">Indexing servers</span><br>
      Each indexing server maintains indexing information. <br>
    </p>
    <p>The REST interface of this server should be the following:</p>
    <p style="background-color: rgb(255, 255, 204); margin-left: 40px;"><code>@Path("/indexer")<br>
        <b>public</b> <b>interface</b> IndexerService {<br>
        <br>
        @GET<br>
        @Path("/search")<br>
        @Produces(MediaType.APPLICATION_JSON)<br>
        List&lt;String&gt; search( @QueryParam("query") String keywords
        ); <br>
        <br>
        @POST<br>
        @Path("/{id}")<br>
        @Consumes(MediaType.APPLICATION_JSON)<br>
        <b>void</b> add( @PathParam("id") String id, Document doc );<br>
        <br>
        @DELETE<br>
        @Path("/{id}")<br>
        <b>void</b> remove( @PathParam("id") String id );<br>
        }</code><br>
    </p>
    <h2>ADDENDUM 29/3/2017:<br>
    </h2>
    <p>&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; The SOAP interface of this
      server,
      is the following: </p>
    <p style="background-color: rgb(255, 255, 204); margin-left: 40px;"><font face="Courier New, Courier, monospace"><b>package</b> api.soap;<br>
        &nbsp;<br>
        @WebService<br>
        <b>public</b> <b>interface</b> IndexerAPI {<br>
        &nbsp;&nbsp;&nbsp; <b><br>
          &nbsp;&nbsp;&nbsp; </b><b>@WebFault</b><b><font color="#990000"><br>
            &nbsp;&nbsp;&nbsp; <font color="#000000">class <font color="#660000">InvalidArgumentException</font>
            </font></font></b><font color="#990000"><font color="#000000"><b>extends</b>
            Exception</font></font><b><font color="#990000"><font color="#000000"> {<br>
              <br>
              &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;</font></font></b><font color="#990000"><font color="#000000"> <b>private</b> <b>static</b>
            <b>final</b>
            <b>long</b> serialVersionUID = 1L;<br>
          </font></font></font><br>
      <font face="Courier New, Courier, monospace"><font color="#990000"><font color="#000000"><font face="Courier New, Courier, monospace"><font color="#990000"><font color="#000000">&nbsp;&nbsp;&nbsp;
                  &nbsp;&nbsp;&nbsp; <b>public</b>
                  InvalidArgumentException() {<br>
                  &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
                  &nbsp;&nbsp;&nbsp; <b>super</b>("");<br>
                  &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; }</font></font></font>&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;
            <br>
            &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; <b>public</b>
            InvalidArgumentException(String msg) {<br>
            &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; <b>super</b>(msg);<br>
            &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; }<br>
          </font></font><b><font color="#990000"><font color="#000000">&nbsp;&nbsp;&nbsp;
              }</font></font><br>
          <br>
          &nbsp;&nbsp;&nbsp; static</b> <b>final</b> String
        NAME="IndexerService";<br>
        &nbsp;&nbsp;&nbsp; <b>static</b> <b>final</b> String
        NAMESPACE="http://sd2017";<br>
        &nbsp;&nbsp;&nbsp; <b>static</b> <b>final</b> String
        INTERFACE="api.soap.IndexerAPI";<br>
        <br>
        &nbsp;&nbsp;&nbsp; /* keywords contains a list of works
        separated by '+'<br>
        &nbsp;&nbsp;&nbsp; &nbsp;* returns the list of urls of the
        documents
        stored in this server that contain all the keywords <br>
        &nbsp;&nbsp;&nbsp; &nbsp;* throws IllegalArgumentException if
        keywords
        is null<br>
        &nbsp;&nbsp;&nbsp; &nbsp;*/<br>
        &nbsp;&nbsp;&nbsp; @WebMethod<br>
        &nbsp;&nbsp;&nbsp; List&lt;String&gt; search(String keywords) <b>throws</b><b>
        </b><b><font color="#990000"> InvalidArgumentException</font></b>;<br>
        <br>
        &nbsp;&nbsp;&nbsp; /*<br>
        &nbsp;&nbsp;&nbsp; &nbsp;* return true if document was added,
        false if
        the document already exists in this server.<br>
        &nbsp;&nbsp;&nbsp; &nbsp;* throws IllegalArgumentException if
        doc is
        null<br>
        &nbsp;&nbsp;&nbsp; &nbsp;*/<br>
        &nbsp;&nbsp;&nbsp; @WebMethod<br>
        &nbsp;&nbsp;&nbsp; <b>boolean</b> add(Document doc) <b>throws</b></font><font face="Courier New, Courier, monospace"><b><font face="Courier
            New, Courier, monospace"> <font color="#660000">
              InvalidArgumentException</font></font></b> ;<br>
        <br>
        &nbsp;&nbsp;&nbsp; /*<br>
        &nbsp;&nbsp;&nbsp; &nbsp;* return true if document was removed,
        false
        if was not found in the system.<br>
        &nbsp;&nbsp;&nbsp; &nbsp;* throws IllegalArgumentException if id
        is null<br>
        &nbsp;&nbsp;&nbsp; &nbsp;*/<br>
        &nbsp;&nbsp;&nbsp; @WebMethod<br>
        &nbsp;&nbsp;&nbsp; <b>boolean</b> remove(String id) <b>throws</b></font><font face="Courier New, Courier, monospace"><b><font face="Courier
            New, Courier, monospace"> <font color="#660000">
              InvalidArgumentException</font></font></b> ;<br>
        }</font><br>
    </p>
    <p><br>
    </p>
    <h2>NOTES: <br>
    </h2>
    <ol>
      <li>To allow clients to distinguish between REST and SOAP service
        instances, the endpoint of the server should should be
        registered at
        the RendezVousServer with attibutes that <b>include </b>the<b>
          key
          "type" </b>with<b> "rest" </b>or<b> "soap"</b>,
        respectively. In the
        absence of the "type" key, the client will assume the server is
        a REST
        server.</li>
      <li>The first argument of the indexer, if present, must be the url
        of
        the rendezvous server -- the test program will start the indexer
        with
        the correct parameters.</li>
    </ol>
    <h2>Access pattern to server<br>
    </h2>
    <p>The indexing service will be used by clients according to the
      following access pattern.</p>
    <p>For adding information for a document, a client will: (1) contact
      the rendez-vous server to get a list of indexing servers; (2)
      select
      one of the indexing servers and invoke the add operation.<br>
    </p>
    <p>For removing information of a document, a client will: (1)
      contact
      the rendez-vous server to get a list of indexing servers; (2)
      select
      one of the indexing servers and invoke the remove operation.</p>
    <p>For searching for information stored in the system, a client
      will:
      (1) contact the rendez-vous server to get a list of indexing
      servers;
      (2) select one of the indexing servers and invoke the search
      operation.</p>
    <p><b><br>
        IMPORTANT</b>: In phase 1, each indexing server only needs to be
      able
      to return information for documents that have been added to that
      server.<br>
    </p>
    <p><br>
      <b>NOTE</b>: We will provide the following components, to be used
      in
      the system being developed and for testing it:</p>
    <ul>
      <li>a library for indexing, supporting an interface similar to the
        interface of the indexing server, which stores information
        locally in a
        node;</li>
      <li>a test program that will execute a sequence of operations and
        check if the returned results are the expected ones. You should
        not
        change the code of the test program.</li>
    </ul>
    <p><br>
    </p>
    <p> </p>
    <h1><a name="base1"></a>Functionalities<br>
    </h1>
    <h2>Up-to date rendez-vous server [2 points]<span style="font-weight: normal;"></span></h2>
    <p><span style="font-weight: normal;">The rendez-vous server must
        maintain up-to-date information about indexing servers. To this
        end,
        the information about an indexing server must be discarded if
        the
        servers stops.</span><span style="font-weight: normal;"></span></p>
    <h2>Automatic discovery of the rendez-vous server [2 points]<span style="font-weight: normal;"></span></h2>
    <p><span style="font-weight: normal;">It should be possible to
        automatically find the rendez-vous server. To this end, the
        rendez-vous
        server should reply to a multicast request with message
        "rendezvous"
        with a string with the URL of the rendez-vous server. The
        multicast
        address and port used by the server can be selected freely.</span></p>
    <h2>Base REST [7 points]<br>
    </h2>
    <p><span style="font-weight: normal;">This consists in the complete
        system, composed by the rendez-vous and indexing servers,
        communicating
        using REST.</span></p>
    <p><span style="font-weight: normal;">As a result of this option,
        you
        should have a working system consisting in a REST-based
        rendez-vous
        server and a set of REST-based indexing servers.<br>
      </span></p>
    <p>Each indexing server only needs to be able to return information
      for
      documents that have been added to that server. However, if a
      remove for
      a given document is invoked in a server, the information for that
      document should be removed independently of the server where it is
      indexed.</p>
    <h2>Base SOAP [6 points]</h2>
    <p><span style="font-weight: normal;">This consists in implementing
        the
        indexing server using SOAP. It is optional to also implement the
        rendez-vous server in SOAP or to use the REST version. The exact
        interfaces that the servers must implement will be introduced in
        lab 3.
        <br>
      </span></p>
    <p><span style="font-weight: normal;">As a result of this option,
        you
        should have a working system consisting in a rendez-vous server
        (either
        using REST or SOAP) and a set of SOAP-based indexing servers.<br>
      </span></p>
    <p>Each indexing server only needs to be able to return information
      for
      documents that have been added to that server. However, if a
      remove for
      a given document is invoked in a server, the information for that
      document should be removed independently of the server where it is
      stored.</p>
    <p> </p>
    <h2>Base SOAP+REST [3 points]</h2>
    <p><span style="font-weight: normal;">This consists in having REST
        and
        SOAP indexing servers capable of working together.</span></p>
    <p><span style="font-weight: normal;">As a result of this option,
        you
        should have a working system consisting in a REST-based
        rendez-vous
        server and a set of indexing servers, some working in REST and
        the
        others working in SOAP.</span></p>
    <p><br>
      <span style="font-weight: normal;"></span></p>
    <br>
    <h1>Notes on faults</h1>
    &nbsp;&nbsp;&nbsp; Regarding failures of the components, you must
    assume:<br>
    <ul>
      <li>the rendez-vous server will not fail;</li>
      <li>indexer servers may fail <span style="font-weight: bold;">permanently</span>
        (fail-stop model) -- note that this will connections to the
        server to
        fail. <br>
      </li>
    </ul>
    <p>Regariding communications, you should assume that communication
      may
      fail <span style="font-weight: bold;">temporarily</span>.<br>
    </p>
    <p><span style="font-weight: bold;"><br>
      </span></p>
    <h1><a name="ambiente"></a>Environment<br>
    </h1>
    <p><span style="font-weight: bold;">IMPORTANT:</span> The project
      must
      be demonstrated in the labs, with servers running in <span style="font-weight: bold;">at least two computers/containers</span>,
      either using existing hardware or student's hardware.</p>
    <p>Y<span style="font-weight: bold;">our system will be tested using
        the test program provided in </span><a style="font-weight: bold;" href="Sd2017-T1-Test.html">this link</a>, which is
      divided in steps
      that test the different functionalities of your program -- you
      should
      use the client to check the progress of your project as you add
      new
      functionalities to your work. <br>
    </p>
    <p>The grading of your project will take into consideration the
      tests
      passed by your system -- so , you should guarantee that your
      systems
      passes as many test as possible (<span style="font-weight: bold; color: red;">projects will be accepted even
        if they do not pass all tests</span>).</p>
    <p><br>
    </p>
    <h1><a name="report"></a>Written Report:<br>
    </h1>
    <p>A written report <b>must</b> be delivered by each group
      describing
      their work and implementation. The report should have <b>at most
        4
        pages</b> <i>(any code that is found relevant should be
        delivered as
        an appendix that goes
        beyond the 4 page limit)</i>.</p>
    <p>The report <b>must</b> cover the following topics.</p>
    <ul>
      <li> General description of the work performed by the students,
        clearly identifying which aspects were completed and fully
        implemented. <br>
      </li>
      <li>Limitations of the delivered code.<br>
        Students should include as annex a table that specifies which
        tests
        their code passed. For the failed tests, students should
        indicate
        whether the test has failed because the tested functionality was
        not
        implemented or because it had a bug.<br>
      </li>
      <li> Interfaces of the servers (both SOAP and REST). </li>
      <li> Clear explanation of the mechanisms (i.e, protocols) employed
        for:
        <ul>
          <li>Discovery of the rendez-vous servers.</li>
          <li>Keeping the rendez-vous server up-to-date.<br>
          </li>
          <li>Handling of faults.</li>
        </ul>
      </li>
      <li>
        Discussion of the implementation decisions taken by the
        students, when applicable, discussing these decisions in light
        of
        possible alternatives (this should include how operations are
        executed,
        with focus on those that the implementation in non-trivial).</li>
    </ul>
    <p>The report can also cover aspects related with difficulties felt
      by
      the students during the execution of the project or other aspects
      that
      the students consider relevant.</p>
    <h1><a name="delivery"></a>Delivery Rules:<br>
    </h1>
    <p>The code of the project should be delivered in electronic format,
      by
      uploading a zip file that includes:<br>
    </p>
    <ul>
      <li>all source files (src directory in the project)</li>
      <li>the sd2017-t1.props file</li>
      <li>the pom.xml file</li>
    </ul>
    <p>Use this <a target="_blank" style="font-weight: bold;" href="https://docs.google.com/a/campus.fct.unl.pt/forms/d/e/1FAIpQLSeuajI7lBadgDDdmztQYsivH0mHnESRTm99If8hYy-vlTktqg/viewform?usp=sf_link">**** link ****</a>
      to deliver your work (NOTE: you must login with your @campus account).<br>
      To keep the size of the zip archive small, zip full eclipse
      project minus the <b>target</b> folder that maven generates with
      the compiled classes and downloaded dependencies.<br>
      <b>IMPORTANT</b>: The name of the zip archive should be:
      SD2017-T1-NUM1.zip or SD2017-T1-NUM1-NUM2.zip</p>
<p><span style="font-weight: bold;">NOTE:</span> You may deliver the project as many times as needed.<br>
 </p>
  </body>
