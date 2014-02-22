![Relateit](./relateitlogo.png) 

----

- [Relateit]
	- [Requirements](#requirements)
	- [Releases](#release)
	- [Indexing and Searching Overview](#indexing-and-searching-overview)
	- [More Indexing](#more-indexing)
		- [Post Document Format](#post-document-format)
		- [How Many Items Can Be Included in a Single Related Item POST?](#how-many-items-can-be-included-in-a-single-related-item-post)
		- [How Many Properties Can I Have for Related Item](#how-many-properties-can-i-have-for-related-item)
		- [Indexing and Searching Logging](#indexing-and-searching-logging)
	- [Searching](#searching)
		- [Indexes Searched Across by Default](#indexes-searched-across-by-default)
			- [Why Use an Alias?](#why-use-an-alias)
	- [Elasticsearch](#elasticsearch)
		- [Elasticsearch Connection Configuration](#elasticsearch-connection-configuration)
		- [Elasticsearch HTTP Connection Configuration](#elasticsearch-http-connection-configuration)
		    - [Elasticsearch HTTP Connection Round Robin](#elasticsearch-http-connection-round-robin)
		- [Elasticsearch Relate Item Type Mapping](#elasticsearch-relate-item-type-mapping)
		- [Elasticsearch Server Configuration](#elasticsearch-server-configuration)
		- [Search pool](#search-pool)
		- [Bulk pool](#bulk-pool)
		- [Get Pool](#get-pool)
		- [Index pool](#index-pool)
	- [JVM Options and Configuration Defaults](#jvm-options-and-configuration-defaults)
		- [Common options for Web Applications](#common-options-for-web-applications)
		- [Searching Heap](#searching-heap)
		- [Indexing Heap](#indexing-heap)
	- [Application Configuration](#application-configuration)
	- [Searching and Indexing Architecture](#searching-and-indexing-architecture)
			- [Searching](#searching-1)
			- [Indexing](#indexing)
	- [Load Testing](#load-testing)
		- [Indexing Load Test Output](#indexing-load-test-output)
		- [Searching Load Test Output](#searching-load-test-output)

----

# Relateit

**relateit** is a simple and easy way to relate one item to several others.  Once several items are related, you can enquire:  "For this item, what are the most frequently related items."

An example use case of this functionality is on a web site to associate the items the people are purchasing.  If a person purchases the books 'Java Performance Tuning', 'Akka Concurrency' and 'Java concurrency in practice' at the same time.  When a second user is browsing 'Java Performance Tuning', you can present that user with related items that this book is most frequently purchased with.  

This application has 3 parts to it:

* A Indexing Web Application (Java)
* A Searching Web Application (Java)
* [Elasticsearch](http://www.elasticsearch.org/ "Elasticsearch") backend

The indexing and searching components (web applications) make use of the [Disruptor](https://github.com/LMAX-Exchange/disruptor "Disruptor") library.  

The search technology Elasticsearch provides the storage, and searching mechanism for providing the related product search.

The Indexing and Searching components do not need to directly be used.  In other words you can just post data in the relevant format into elasticsearch, and then perform searching directly against elasticsearch to obtain the most frequently related items.   However, it is the Indexing and Searching components that provide a means of batching indexing and searching requests that are being send to elasticsearch.

----

## Requirements ##

* JDK 7 (recommended jdk7u40+).
* Java Web Application Server (Tested on Tomcat).
* Requires Servlet 3
* Elasticsearch 0.90.11  (1.0.2)


## Releases ##

Currently releases for the indexing and searching war can be found in the sonatype releases repo:

- https://oss.sonatype.org/content/repositories/releases/org/greencheek/related

This mean you can download manually, or via maven.

Apologies for the lack of other options at the moment, and formats.  An rpm download format (with possible customization)
will be available in the future.

### Searching War ###

* 1.0.2 : https://oss.sonatype.org/content/repositories/releases/org/greencheek/related/related-web-searching/1.0.2/related-web-searching-1.0.2.war coming soon (0.90.11)
* 1.0.1 : https://oss.sonatype.org/content/repositories/releases/org/greencheek/related/related-web-searching/1.0.1/related-web-searching-1.0.1.war (0.90.9)
* 1.0.0 : https://oss.sonatype.org/content/repositories/releases/org/greencheek/related/related-web-searching/1.0.0/related-web-searching-1.0.0.war

### Indexing War ###

* 1.0.2 : https://oss.sonatype.org/content/repositories/releases/org/greencheek/related/related-web-indexing/1.0.2/related-web-indexing-1.0.2.war coming soon (0.90.11)
* 1.0.1 : https://oss.sonatype.org/content/repositories/releases/org/greencheek/related/related-web-indexing/1.0.1/related-web-indexing-1.0.1.war (0.90.9)
* 1.0.0 : https://oss.sonatype.org/content/repositories/releases/org/greencheek/related/related-web-indexing/1.0.0/related-web-indexing-1.0.0.war

___

## Indexing and Searching Overview ##

The index web application is POSTed data containing a group of related items, i.e. for example a purchase.  A post request is as follows:

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"de",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "type":"map"
          },
          {
             "id":"2",
             "type":"compass"
          },
          {
             "id":"3",
             "type":"torch"
          },
          {
             "id":"4",
             "type":"torch",
             "channel":"uk"
          }
       ]
    }'

 
The indexing application returns a 202 accepted status code to the client that issued the indexing request.  This indicate to the client that the request has been accepted for processing and indexing (this doesn't mean the json data is valid.  It just means the POST data is a valid binary payload).  

It is at this point the POSTed data is assembled into several documents.  For example, for the above request; 3 JSON documents will be assembled and submitted to elasticsearch for storage and indexing:

    {
        "id": "1" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "2","3","4"],
        "type": "map",
        "site": "amazon",
        "channel": "de"
    }

    {
        "id": "2" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "1","3","4"],
        "type": "compass",
        "site": "amazon",
        "channel": "de"
    }

    {
        "id": "3" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "1","2","4"],
        "type": "torch",
        "site": "amazon",
        "channel": "de"
    }
    
    {
        "id": "4" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "1","2","3"],
        "type": "torch",
        "site": "amazon",
        "channel": "uk"
    }

The json element "related-with", will be used during search, to provide faceting information.  It is this facet that allows us to say:

* For this id, what are the 5 top most frequently **related-with** ids.

The search web application is then called to request the frequently related items for a product (a GET request):

    curl -v -N http://10.0.1.29:8080/searching/frequentlyrelatedto/1

Which returns json data containing the list of items (their id's) that are frequently related to that item:

    {
        "response_time": "2", 
        "results": [
            {
                "frequency": "1", 
                "id": "4"
            }, 
            {
                "frequency": "1", 
                "id": "3"
            }, 
            {
                "frequency": "1", 
                "id": "2"
            }
        ], 
        "size": "3", 
        "storage_response_time": "0"
}

Note the search does not only provide the ability to search on the id.  It allows you to filter the result based on a property of the indexed document.  For example, from the initial POST there was 4 related items:

* 3 of those had the property: **channel: de**
* 1 of those had the property: **channel: uk**

The item that had overridden the **"channel"** property was the following:

    {
        "id": "4" ,
        "date": "2013-12-24T17:44:41.943Z",
        "related-with": [ "1","2","3"],
        "type": "torch",
        "site": "amazon",
        "channel": "uk"
    }

In the above, there are 3 extra elements that can be searched on:

* type
* site
* channel

As a result you can say.  Give with the frequently related product that are mostly purchased with product **1**, filtered the results to reduce to that of "tourches", i.e. type=torch:

    curl -v -N http://10.0.1.29:8080/searching/frequentlyrelatedto/1?type=torch

The result is:

    {
        "response_time": "11", 
        "results": [
            {
                "frequency": "1", 
                "id": "4"
            }, 
            {
                "frequency": "1", 
                "id": "3"
            }
        ], 
        "size": "2", 
        "storage_response_time": "2"
    }

You can go further and search for torches, just in channel uk, which is:

    curl -v -N "http://localhost:8080/searching/frequentlyrelatedto/1?type=torch&channel=uk" | python -mjson.tool

Which will result in just the one related item:

    {
        "response_time": 3, 
        "results": [
            {
                "frequency": "1", 
                "id": "4"
            }
        ], 
        "size": "1", 
        "storage_response_time": 1
    }

Currently the search result just returns the id's of the related items, and the frequency by which it was related with the searched for item it.   It *DOES NOT* return the matching document's information.  

The reason behind not returning the matching documents source, is that there could be literally hundreds of matching documents.  For example, the dvd "The Raid" could be bought many many times, with a range of other products.  For the below related item POSTs, the dvd "The Raid", is associated with "Enter the dragon" and "kick boxer".  Where the it is associated with "kick boxer" twice, and therefore "kick boxer" is the most frequently related item.  In the backend (elasticsearch), there are two actual physical documents that represent "kick boxer".  If we are to return the matching documents for the "most frequently related", we have to return the source both documents.  Expanding this further you could quite easily have a item related to another 100's of times; which would mean 100's of matching documents.  It is for this reason the content of that matches are not returned.

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "title":"The Raid",
             "type":"dvd"
          },
          {
             "id":"2",
             "title":"Enter the dragon",
             "type":"dvd"           
          }
       ]
    }'

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "title":"The Raid",
             "type":"dvd"
          },
          {
             "id":"2",
             "title":"kick boxer",
             "type":"dvd"           
          }
       ]
    }'
    
    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d '
    {
       "channel":"uk",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "title":"The Raid",
             "type":"dvd"
          },
          {
             "id":"2",
             "title":"kick boxer",
             "type":"dvd"           
          }
       ]
    }'

___

## More Indexing ##

When a group of related items are indexed, by default they are stored in elasticsearch in a dated index, for example "relateditems-YYYY-MM-DD":

Each date based index, is an index of it's own in elasticsearch.  If you were searching directly against elasticsearch you could independently search the one dated index.  

The date of the index is based on the UTC date of either:

* The date contained within the RELATED ITEMS POST (converted to UTC)
* The current date set on the server on which the indexing application is running.

The previous POST contained no date.  However, the following example POST contains a date that is in UTC timezone.  As an index named "**relateditems-2013-12-25**" will be created and the 4 related item documents indexed within that index.

    {
        "channel": "de", 
        "date": "2013-12-25T09:44:41.943", 
        "items": [
            {
                "id": "1", 
                "type": "map"
            }, 
            {
                "id": "2", 
                "type": "compass"
            }, 
            {
                "id": "3", 
                "type": "torch"
            }, 
            {
                "channel": "uk", 
                "id": "4", 
                "type": "torch"
            }
        ], 
        "site": "amazon"
    }


As mentioned above, if the date in the related items POST contains a date with time zone information, it will be converted to UTC, and then the date used.  For example, the below POST contains:


    "date": "2013-12-24T09:44:41.943+10:00"

Given this date, the 4 related item documents will be indexed within the index named:

    relateditems-2013-12-23

The reason being that "2013-12-24T09:44:41.943+10:00" is "2013-12-23T23:44:41.943" in UTC timezone.  The resulting 4 related documents will contain the UTC timestamp.

    {
        "channel": "de", 
        "date": "2013-12-24T09:44:41.943+10:00", 
        "items": [
            {
                "id": "1", 
                "type": "map"
            }, 
            {
                "id": "2", 
                "type": "compass"
            }, 
            {
                "id": "3", 
                "type": "torch"
            }, 
            {
                "channel": "uk", 
                "id": "4", 
                "type": "torch"
            }
        ], 
        "site": "amazon"
    }

An example document indexed in "**relateditems-2013-12-23**" looks as follows (taken from elasticsearch head):

    {
        _index: relateditems-2013-12-23
        _type: related
        _id: EmHW1qQBQv2BMgmQAlyMiA
        _version: 1
        _score: 1
        _source: {
            id: 3
            date: 2013-12-23T23:44:41.943Z
            related-with: [
                4
                1
                2
            ]
            type: torch
            site: amazon
            channel: de
        }
    }

The document itself is:

    {
        "id": "3" ,
        "date": "2013-12-23T23:44:41.943Z",
        "related-with": [ "4","1","2"],
        "type": "torch",
        "site": "amazon",
        "channel": "de"
    }    

____

### Post Document Format ###

The related items document that is POST'ed to the indexing web application has a couple of keys that it refers to:

* "**items**": An array of items that are related, i.e. just been purchased together
* "**id**":    The id of the item, this is your identify for the item, for example the Product Id.
* "**date**":  The date at which the related items were created, i.e. the purchase time

Example:

    {
        "date": "2013-12-24T09:44:41.943+10:00", 
        "items": [
            {
                "id": "1"      
            }, 
            {
                "id": "2" 
            } 
        ] 
    }
 
Or in short form:

    {
        "date": "2013-12-24T09:44:41.943+10:00", 
        "items": [ "1","2" ]
    }

The short form of the post makes the assumption that the strings in the "**items**" array are the "**id**"'s 

As previously seen the indexing POST document can contain extra keys and values that are associated to either the related items POST in it's entirety, or can be specific to a particular item in the related items document.

For example:

    {
        "date": "2013-12-24T09:44:41.943+10:00", 
        "site": "amazon", 
        "channel" : "uk",       
        "items": [
            {
                "department" : "electronics",
                "category" : "storage",
                "type" : "hard disk",
                "id": "1",      
            }, 
            {
                "department" : "electronics",
                "category" : "notebooks",
                "type" : "macbook pro",
                "memory" : "8gb",
                "channel" : "de",
                "id": "2", 
            } 
        ] 
    }

Given the above the key/value: **"site": "amazon"**, will apply to all the related item documents that are indexed in elasticsearch (2 documents in the above).  The key/value pairs within the "items" array will apply just to that item's document that is indexed.  

Document with "id": "1" will have the key/values:

    "date": "2013-12-23T23:44:41.943"
    "site": "amazon", 
    "channel" : "uk",    
    "department" : "electronics",
    "category" : "storage",
    "type" : "hard disk",    

Document with "id" : "2" will have the key/values:

    "date": "2013-12-23T23:44:41.943"
    "site": "amazon", 
    "channel" : "de",    
    "department" : "electronics",
    "category" : "notebooks",
    "type" : "macbook pro",
    "memory" : "8gb"

If an element exist in the item's property that was defined in the parent enclosing document, then the item's value takes precedence and overrides that of the enclosing document's setting.

With the above you can see that the second item has an extra field "memory", than that of the first document.  Whilst it does not make much difference to the backend (elasticsearch) or the web applications (searching or indexing), for consistencies sake you shouldn't really have key existing in one related item that do not appear in the.  However, it is entirely up to you.  

---

### How Many Items Can Be Included in a Single Related Item POST? ###

By fault 10 related items per POST request can be handled.  By default if there are 11 items in the indexing request; the last item is silently ignored (a warning is output in the logs).  The below are the properties that are available for configuration, for adjusting these defaults

    * related-item.max.number.related.items.per.index.request = 10
    * related-item.max.related.item.post.data.size.in.bytes = 10240
    * related-item.indexing.discard.storage.requests.with.too.many.relations = false

---

### How Many Properties Can I Have for Related Item ###

The answer to this question is that you can have as many properties as you like per indexed relate item.  However, in order to have as many items as you like you need to pay for that, in terms of memory allocated to the application, or reduction in the ring buffer size, and or length of the property keys/values.

The following properties are available for configuration, show with their defaults.  As a result if you know a related item will have more than 10 properties (remember this leaves you with 7 configurable properties of your choosing; id, date and related-with take already taken)

    * related-item.max.number.related.item.properties = 10
    * related-item.additional.prop.key.length = 30 (characters)
    * related-item.additional.prop.key.length = 30 (characters)
    * related-item.indexing.size.of.incoming.request.queue = 16384
    
----

### Indexing and Searching Logging ###

The indexing and searching application uses log4j2, the default log configuration is as follows.  Log4j2 is used due to use of the disruptor framework to perform logging asynchronously.  In order for the default logging configuration to kick in the following property is required

    -DLog4jContextSelector="org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"

If you don't like the look of the below configuration you can specify your own configuration file via:  

    -Dlog4j.configurationFile=<absolute path to my file>

If the below configuration looks adequate, then you can customs the configurations with the following system properties:

    -Drelated-item.searching.log.file=<absolute location of file>
    -Drelated-item.searching.log.level=ERROR
    -Drelated-item.indexing.log.file=<absolute location of file>
    -Drelated-item.indexing.log.level=ERROR

The defaults are "WARN" and searching.log/indexing.log either in CATALINA_BASE/logs/ or java.io.tmpdir

The log4j2.xml configuration files are as follows:

*Searching*

```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration status="WARN" monitorInterval="120">
        <appenders>
            <RollingRandomAccessFile name="SEARCHING" fileName="${sys:related-item.searching.log.file}"
                                 immediateFlush="false" append="true"
                                 filePattern="${sys:related-item.searching.log.file}-%d{yyyy-MM-dd}-%i.log.gz">
                <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
                <Policies>
                    <TimeBasedTriggeringPolicy />
                    <SizeBasedTriggeringPolicy size="50 MB"/>
                </Policies>
            </RollingRandomAccessFile>
        </appenders>
        <loggers>
            <root level="${sys:related-item.searching.log.level}" includeLocation="false">
                <appender-ref ref="SEARCHING"/>
            </root>
        </loggers>
    </configuration>
```

*Indexing*

```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration status="WARN" monitorInterval="120">
        <appenders>
            <RollingRandomAccessFile name="INDEXING" fileName="${sys:related-item.indexing.log.file}"
                                 immediateFlush="false" append="true"
                                 filePattern="${sys:related-item.indexing.log.file}-%d{yyyy-MM-dd}-%i.log.gz">
                <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
                <Policies>
                    <TimeBasedTriggeringPolicy />
                    <SizeBasedTriggeringPolicy size="50 MB"/>
                </Policies>
            </RollingRandomAccessFile>
        </appenders>
        <loggers>
            <root level="${sys:related-item.indexing.log.level}" includeLocation="false">
                <appender-ref ref="INDEXING"/>
            </root>
        </loggers>
    </configuration>
```


----
## Searching ##


### Indexes Searched Across by Default ###

By default the indexes that are searched in elasticsearch are any index that have a name starting with "**relateditems-**".  In other words a wild card search across all dated indexes starting with **relateditems-**

The prefix of the index name is controlled by the following configuration parameter.  A hyphen "-", will added to the end of the prefix, and any subsequent indexing will index documents into a dated index:

    -Drelated-item.storage.index.name.prefix

For example with the setting:

    -Drelated-item.storage.index.name.prefix=related

Documents will be indexed and Searching in/from indexes named: "**related-YYYY-MM-DD**".

It is also possible to use an alias against which to perform searches, rather than performing a wild search search. (**note** The alias does not apply to indexing).

The below gives an example of the curl request used to set up an index in elasticsearch.  It creates an alias "**related**"", which is an alias for the indexes: "**relateditems-2013-12-23**" and "**relateditems-2013-12-24**".  When "**related**" is used to search, the search will be performed only against those two indexes.

    curl -XPOST localhost:9200/_aliases -d '
    {
        "actions": [
            { "add": {"alias": "related", "index": "relateditems-2013-12-23"} },
            { "add": { "alias": "related","index": "relateditems-2013-12-24"} }
        ]
    }'

To tell the Search Web Application to use that alias, instead of the wildcard index search, you specify the alias name using the following parameter:

    -Drelated-item.storage.index.name.alias=related

#### Why Use an Alias? ####

An alias can give you added flexibility over the content that is being searched.  However, this comes at the cost of added complexity from having to maintain the alias. 

An example usage for an alias could be the following.  Imagine one day you are having a promotion for a new selection of products.  You could choose to promote those products against a selection of other products.  One way to do this is to re-associate the alias to a prepared index of those products and the associated items:

    curl -XPOST localhost:9200/_aliases -d '
    {
        "actions": [
            { "add" :   { "alias" : "related", "index": "promotion-2013-12-25"   } },
            { "remove": { "alias" : "related", "index": "relateditems-2013-12-23"} },
            { "remove": { "alias" : "related", "index": "relateditems-2013-12-24"} }
        ]
    }'

The above removes the existing mappings and creates the new mapping.  This can be done a runtime, without starting either elasticsearch or the searching application.  However, the downside here is the maintenance of the alias.  

The alias cannot use a wildcard, and it needs to point to a valid index that exists.  Therefore, a maintenance script needs to create that would periodically run to update the alias mapping to point to new indexes.

Unfortunately at the moment support for assigning index alias's at index creation time does not currently exist (https://github.com/elasticsearch/elasticsearch/pull/2739 and https://github.com/elasticsearch/elasticsearch/issues/4920).

For more information about index alias in elasticsearch please read:

    http://www.elasticsearch.org/blog/changing-mapping-with-zero-downtime/


---

## Elasticsearch ##

As mentioned above, the related item data is stored in elasticsearch and the ability to find the frequently related items,
for an item, is provided by that of elasticsearch and its faceting.

The indexing and searching web applications use the elasticsearch java library.

The means by which the indexing and searching applications talk to elastic is by using the elasticsearch binary transport protocol.
Meaning, the version of the client embedded within the web applications (indexing and searching), *MUST* match that of the elasticsearch server.
Also, the version of JAVA on the client and the server *MUST* be the same.

    * Current embedded elasticsearch version is: **0.90.11**

By default both applications use the Transport protocol to connect to elasticsearch with sniffing enabled:

    * client.transport.sniff : true

Sniffing means that you only need to specifiy a couple of hosts, and the client will gleen information on the rest of the cluster
through those nodes.  These means that you can add new nodes to your cluster, and they will be found by the client.

The reason behind no support for HTTP endpoint (in versions 1.0.0 and 1.0.1) is just to focus on using the most performant
client option, which is that of the transport client.   HTTP support is *now* available in version 1.0.2+.

The HTTP support requires either extra configuration at your side (i.e. a load balancer) to load balance over your ES nodes,
Or you can use in simple internal round robin implementation in the HTTP client support.

The default connection protocol is that of **transport**, but the it is relatively easy to use *http*; as shown below.

----
### Elasticsearch Connection Configuration ##

The defaults for indexing and searching have been set based on a JVM the is running 1GB with 128m of PermGen
(The specific configuration for these JVM Parameters can be found below).

At minimum the only configuration required is the connection details for your elasticsearch installation:

    * related-item.elastic.search.transport.hosts=10.0.1.19:9300

This can be a comma separated list of hosts:

    * related-item.elastic.search.transport.hosts=10.0.1.19:9300,10.0.1.29:9300

By default the application uses the TRANSPORT client to connect to elastic search.
If you only specify one host, but you have 2 nodes in your elasticsearch cluster, the transport client is enabled by default to
sniff (ask the node for information about other nodes in the cluster), and obtain a list of other nodes to connect to.


----

### Elasticsearch HTTP Connection Configuration

As of release 1.0.2+ an http client connection has been made available, [Issue 2](https://github.com/tootedom/related/issues/2).
The http client connection means that you do not have to have ES client library, as that of the ES server.  This means
you can upgrade the server with less fear of breaking the client implementation.

The Http client library used is that of the [AsyncHttpClient](https://github.com/AsyncHttpClient/async-http-client).
Even though the asynchronous features of this http client library have not been taken advantage of, the easy of use of
 the library made it a preferred choice over that of something like Apache's Http Client.

To enable the HTTP client connection you need to specify the property:

    -Drelated-item.es.client.type=http

With this enabled the indexing and searching web applications use the HTTP connection factory to talk to the ES server's
http endpoint.   In order for the app to know what to talk to you specify the following property (by default it is *http://127.0.0.1:9200*):

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200


You can specify multiple hosts, by comma separating them:

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200,http://10.0.1.29:9200,http://10.0.1.39:9200,http://10.0.1.49:9200

The http client will round robin requests over the given number of endpoints* (*see below for round robin details*).

By default the HTTP client will run a background scheduling task that talks to each ES endpoint, checking for any newly
added ES nodes.  If a node has been added the HTTP client is notified of the new host, and it will be made available for
round robin allocation of http requests.  The background thread runs every 15 minutes hitting the following url:

   <host>:<port>/_nodes/http

from the returned json it parses the http endpoint information building a list of available connections.  It is that
list that forms the new list of load balanced nodes.

This sniffing of available ES servers can be disabled with the following property:

    -Drelated-item.elastic.search.http.nodesniffing.enabled=false

If you wish to descrease the frequency of which the sniffing takes place use the following property:

    -Drelated-item.elastic.search.http.nodesniffing.retry.interval=30

The default unit is minutes.  This can be changed with the property:

    -Drelated-item.elastic.search.http.nodesniffing.retry.interval.unit=secs|mins|hours|days

----

#### Elasticsearch HTTP Connection Round Robin

The Http Client performs round robin load balancing of requests over the set of available ES http nodes.
So for example given 2 hosts:

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200,http://10.0.1.29:9200

Each host will be sent an equal number of requests.  The round robin load balancing implementation uses an array
size to a power of 2, to loop over the array of available hosts.  This means for a list of 3 hosts.  The load is *NOT*
spread evenly over the nodes.  Given 3 hosts:

    -Drelated-item.elastic.search.http.hosts=http://10.0.1.19:9200,http://10.0.1.29:9200,http://10.0.1.39:9200

The host list internally will be expanded to a power of 2 (i.e 4 available hosts for load balancing).  The extra hosts
are made up from the existing list of actual available.   Which means that for 3 defined hosts.
1 host will get more traffic than then others.  For example the repetition may something like the follwoing list, where
*http://10.0.1.19:9200* is in the list twice.

    http://10.0.1.19:9200,http://10.0.1.29:9200,http://10.0.1.39:9200,http://10.0.1.19:9200


----

### Elasticsearch Relate Item Type Mapping

When the indexing and searching applications talk to elasticsearch they search for documents within the index "relateditems-YYYY-MM-DD" for the document type "related".  As previously mentioned the defined properties require for the "related" type are: 

* id 
* related-with
* date

When indexing documents in elasticsearch, if a type (i.e. "related") does not have an associated mapping then a dynamic mapping of a document's json properties are created.  Which may or may not be what is required.  As a result, you should define a mapping for the type.  The mapping for the related type should at minimum be the following:

    curl -XPUT http://localhost:9200/_template/relateditems -d '{
        "template" : "relateditems*",
        "settings" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 1,
            "index.refresh_interval" : "5s",
            "index.store.compress.stored" : false,
            "index.query.default_field" : "id",
            "index.routing.allocation.total_shards_per_node" : 1,
            "indices.memory.index_buffer_size" : 30
        },
        "mappings" : {
            "related" : {
               "_all" : {"enabled" : false},
               "dynamic" : false,
               "properties" : {
                  "id": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "related-with": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "date": { "type": "date", "index": "not_analyzed", "store" : "no" }
               }
            }
        }
    }'

If the related documents you are indexed are going to have more properties (i.e. channel, type, etc).  You need to expand upon the mapping above to detail those properties.  A guide to mapping can be found [In the following elasticsearch documentation](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html)

    curl -XPUT http://10.0.1.19:9200/_template/relateditems -d '{
        "template" : "relateditems*",
        "settings" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 1,
            "index.refresh_interval" : "5s",
            "index.store.compress.stored" : false,
            "index.query.default_field" : "id",
            "index.routing.allocation.total_shards_per_node" : 1,
            "indices.memory.index_buffer_size" : 30
        },
        "mappings" : {
            "related" : {
               "_all" : {"enabled" : false},
               "dynamic" : false,
               "properties" : {
                  "id": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "related-with": { "type": "string", "index": "not_analyzed", "store" : "yes" },
                  "date": { "type": "date", "index": "not_analyzed", "store" : "no" },
                  "channel" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" },
                  "site" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" },
                  "type" : {"type" : "string" , "index" : "not_analyzed", "store" : "no" }
               }
            }
        }
    }'



### Elasticsearch Server Configuration ###

The elasticsearch server itself also requires some configuration.  By default out of the box elastic search will use multicast to locate other nodes in the cluster, and will locally store indexes inside the *data/* directory in it's download installation location.  You more than like want to:

    * Move to unicast if your network does not cope with multicast traffic routing well (i.e. multiple data centres, etc.)
    * Move the local storage to a raid array, with raid 1, 5 or raid 1+0 (10), away from the data/ directory.  So that you can update the elasticsearch binaries without affecting the data indexed.

The elasticsearch configuration file (**config/elasticsearch.yml**), needs to be updated to reflect the default cluster name that the indexing and searching application will be looking for the elasticsearch cluster/nodes to be operating with (the default being "**relateditems**").  The name of the cluster is controlled by the following property on the Searching or Indexing web application:

    * related-item.storage.cluster.name

Therefore the elasticsearch configuration (**config/elasticsearch.yml**) should have the following set:
 
    cluster.name: relateditems

There are several other properties that are not by default in the elasticsearch.yml file, that assist in its operations (searching, bulk operations, getting and indexing).  The following configuration reduces the size of the queue, and the maximum number of threads that elasticsearch can run of the given operations.  By changing the defaults we are allowing existing operations to complete, without flooding it with more requests until it is unable to cope with the load.  As a result we bound the size of the pools and queues, in order to apply back pressure to the request's origin (I.e. The search application and the indexing application)

These pool settings are as follows.  The settings a highly dependent upon the size of your elastic search cluster.  This is just a set of recommendations.

### Search pool

    threadpool.search.type: fixed
    threadpool.search.size: 20
    threadpool.search.queue_size: 100000

### Bulk pool
    threadpool.bulk.type: fixed
    threadpool.bulk.size: 25
    threadpool.bulk.queue_size: 100000

### Get Pool
    threadpool.get.type: fixed
    threadpool.get.size: 1
    threadpool.get.queue_size: 1

### Index pool
    threadpool.index.type: fixed
    threadpool.index.size: 20
    threadpool.index.queue_size: 100000
----

## JVM Options and Configuration Defaults #

The default configuration for indexing and searching are based on a 1GB heap
(-Xmx1024m -Xms1024m) configuration.  It is for this default configuration
that the below JVM options and Heap configuration is specified.

The specific recommended (tested against) JVM options for searching and indexing
are listed below (jdk7 - the following options *WILL NOT* work on jdk6).
The JVM options slightly differ between searching and indexing.  The common options
are listed and then the differences listed:

### Common options for Web Applications

    -XX:CMSInitiatingOccupancyFraction=85
    -XX:MaxTenuringThreshold=15
    -XX:CMSWaitDuration=70000    
    -XX:MaxPermSize=128m
    -XX:ParGCCardsPerStrideChunk=4096
    -XX:+UseParNewGC
    -XX:+UseConcMarkSweepGC
    -XX:+UseCMSInitiatingOccupancyOnly    
    -XX:+UnlockDiagnosticVMOptions
    -XX:+AggressiveOpts
    -XX:+UseCondCardMark

----
Below shows the heap configuration for indexing and search.  The difference between
the two is that of the eden space.

### Searching Heap ###

    -Xmx1024m
    -Xmn700m
    -Xms1024m
    -Xss256k

### Indexing Heap ###

    -Xmx1024m
    -Xmn256m
    -Xms1024m
    -Xss256k

----


## Application Configuration ##

The Searching and Indexing web applications can be configured via a wide range of properties.  These can either be set using System Properties:

* -Drelated-item.max.number.related.item.propetties=10

Or by using a yaml configuration file.  The following will list all the properties that are available for configuration, along with their use.  Some properties are specifically for searching, others specifically for indexing, and others for both.  This will be noted.


| Property Name   | usage | Searching/Indxing/ALL |
| ----------------| ----- | --------------------- |
* related-item.safe.to.output.index.request.data | Writes to logs (when DEBUG) the index request data | Indexing |
* related-item.max.number.related.item.properties | The max number of properties a related item can have.  More properties than this will be silently discarded.  There is no guarantee of ordering | Indexing | 
* related-item.max.number.related.items.per.index.request | The max number of related items in a single index POST request | Indexing |
* related-item.related.item.id.length | The max number of characters that the "id" of a related items can have | All |
* related-item.max.related.item.post.data.size.in.bytes | max size in bytes of the POST data for an index request| Indexing |
* related-item.min.related.item.post.data.size.in.bytes | The minimum size, in bytes, of the POSTed json data for an index request | Indexing |
* related-item.additional.prop.key.length | The max number of characters a property name can have| All |
* related-item.additional.prop.value.length | the max number of characters a property value can have | All |
* related-item.indexing.size.of.incoming.request.queue | Size of the ring buffer that accepts incoming indexing POST requests | Indexing |
* related-item.indexing.size.of.batch.indexing.request.queue  | The size of the ring buffer for each indexing processor that batch posts indexing requests to elasticsearch | Indexing |
* related-item.indexing.batch.size | The max number of related item objects (a single index request will have many related item objects), that can be sent for batching indexing to elastic search.| Indexing |
* related-item.searching.size.of.related.content.search.request.queue | Size of the ring buffer that accepts incoming search requests | Searching |
* related-item.searching.size.of.related.content.search.request.handler.queue | Size of the ring buffer for each search processor that submits search requests to elasticsearch | Searching |
* related-item.searching.size.of.related.content.search.request.and.response.queue | Size of the ring buffer that is used to store incoming Request AsyncContext objects for later retrieval | Searching |
* related-item.searching.max.number.of.search.criteria.for.related.content | number of additional properties that will be searched on | Searching |
* related-item.searching.number.of.expected.like.for.like.requests | The number of search request that we expect to be similar| Searching |
* related-item.searching.key.for.frequency.result.id | The key used for the id field in the search result json| Searching |
* related-item.searching.key.for.frequency.result.occurrence | The key used for the frequency in the search results json | Searching |
* related-item.searching.key.for.storage.response.time | Key used to represent how long the elasticsearch request took, in the json response doc | Searching | 
* related-item.searching.key.for.search.processing.time | Key used to represent how long the complete search request took.  It is the key used in the response json | Searching |
* related-item.searching.key.for.frequency.result.overall.no.of.related.items | key in the search response used to represent the number of frequencies returned | Searching |
* related-item.searching.key.for.frequency.results | key in the search response json under which the frequencies are found | Searching |
* related-item.searching.request.parameter.for.size | request parameter used to specify the max number of frequencies to return| Searching | 
* related-item.searching.request.parameter.for.id | parameter used to associate the id in a map of request parameters | Searching |
* related-item.searching.default.number.of.results | default number of search result (frequencies) to return | Searching |
* related-item.searching.size.of.response.processing.queue | size of ring buffer for processing search results and sending json response to the awaiting AsyncContext | Searching |
* related-item.indexing.number.of.indexing.request.processors | number of processors used to perform indexing (sending batch indexing requests) to elasticsearch | Indexing | 
* related-item.searching.number.of.searching.request.processors | The number of ring buffers (processors) that will be sending search requests to elasticsearch | Searching |
* related-item.storage.index.name.prefix | The name of the index used in elasticsearch for storing related item documents (i.e. relateditems-<YYYY-MM-DD>) | All |
* related-item.storage.index.name.alias | The name of the index alias against which to search (http://www.elasticsearch.org/blog/changing-mapping-with-zero-downtime/) | All |
* related-item.storage.content.type.name | The index type | All |
* related-item.storage.cluster.name | The name of the elasticsearch cluster | All |
* related-item.storage.frequently.related.items.facet.results.facet.name | The property used for naming the facet during the search request to elastic search  | Searching |
* related-item.storage.searching.facet.search.execution.hint | Used during search request to elastic search.  The setting of 'map' is the default.  Makes request much much faster | Searching |
* related-item.indexing.key.for.index.request.related.with.attr | The key used in the indexed document for the storing the related ids | All |
* related-item.indexing.key.for.index.request.date.attr | The key used in the indexed document for the date attribute | All |
* related-item.indexing.key.for.index.request.id.attr | The key against which the id is stored in the indexed document | All |
* related-item.indexing.key.for.index.request.item.array.attr | The key in the incoming user json indexing request that contains the list of items | All |
* related-item.elastic.search.client.default.transport.settings.file.name | name of the elastic search file containing the transport client settings (defaults) | All |
* related-item.elastic.search.client.default.node.settings.file.name | name of the elasticsearch file containing the node client settings (defaults) | All |
* related-item.elastic.search.client.override.settings.file.name | name of the elasticsearch file than can be distributed to override the default node/transport settings | All |  
* related-item.searching.frequently.related.search.timeout.in.millis | timeout in millis for elasticsearch requests | All |
* related-item.storage.location.mapper | day/hour/min used to convert date to a string used for creating the index name in which documents are stored | All |
* related-item.searching.timed.out.search.request.status.code | the http status code when a timeout occurs | Searching |
* related-item.searching.failed.search.request.status.code | the http status code when a search request fails to talk to elasticsearch | Searching |
* related-item.searching.not.found.search.request.status.code | the http status code when no search result is found | Searching |
* related-item.searching.found.search.results.handler.status.code | the http status code when a match is found | Searching |
* related-item.searching.missing.search.results.handler.status.code | the http status code when we cannot handle the json search response | Searching |
* related-item.wait.strategy | The type of ring buffer wait strategy: yield/busy/sleep/block | All |
* related-item.es.client.type | The type of elasticsearch client to use | All |
* related-item.indexing.indexname.date.caching.enabled | caching of index date | All |
* related-item.indexing.number.of.indexname.to.cache | number of index names to cache | All |
* related-item.indexing.replace.old.indexed.content | replace existing content (false) | Indexing | 
* related-item.use.separate.repository.storage.thread | Use a separate thread for performing indexing | Indexing |
* related-item.indexing.discard.storage.requests.with.too.many.relations | silently discard related items in the indexing request it there are too many.  Indexes up to the max, discards the others | Indexing |
* related-item.elastic.search.transport.hosts | The host:port,host:port contain the unicast addresses of the search nodes in elastic search to talk to | All |
* related-item.elastic.search.default.port | the default port if not specified to talk to in elasticsearch | All |
* related-item.searching.use.shared.search.repository | Whether the search processors use a shared connection to elastic search | Searching | 
* related-item.searching.response.debug.output.enabled | output the response json being sent to the client, also to a log file.  | Searching |


By default the Searching and Indexing web applications will look for a yaml configuration file from which to load the configuration details.  Any settings in the configuration file, override the defaults.  Any system properties set will override the settings that are contained within the yaml configuration.  

By default the yaml file **related-items.yaml** is looked for on the class path.  The location of the file can be specified by the property, **related-items.settings.file**, for example:

* -Drelated-items.settings.file=/etc/relateditems.yml

The yaml file, may look like the following:

    related-item:
           searching:
                  number.of.searching.request.processors: 16
                  size.of.related.content.search.request.handler.queue: 1024

           indexing:
                  size.of.batch.indexing.request.queue: 4096


With the above in place the following properties are overridden:

* related-item.searching.number.of.searching.request.processors
* related-item.searching.size.of.related.content.search.request.handler.queue
* related-time.indexing.size.of.batch.indexing.reqeust.queue

If a system properties was set (-Drelated-item.searching.number.of.searching.request.processors=2), that would override the setting in the yaml file.

----

## Searching and Indexing Architecture ##

The below shows a couple of simple high level architecture diagrams for the indexing and searching.

#### Searching ####

![Search Application Architecture](./SearchExecutionArch.png)

#### Indexing ####


![Index Application Architecture](./IndexExecutionArch.png)


## Load Testing ##

Below show the load testing results from indexing and searching tests performed against the application during development and testing.  You will obviously have different results based on your representative indexing data and searches.  

The load tests results are from running a 1GB heap and running indexing and searching completely independently of each other.

Elasticsearch version 0.90.9/0.90.11 is running on 2 hosts:

* Mac mini 2.3 GHz Core i5 (I5-2415M)
* macbook pro 17" 2.5 GHz Core i7 (I7-2860QM) 

The indexing and searching applications are running on:

* Dell poweredge t420, 2 cpu Intel(R) Xeon(R) CPU E5-2407 2.20GHz.
* tomcat 7u42, NIO connector.
* centos 6.5

The connection between the dell t420 and the macbook pro is wifi 5g, and to mac mini 100mbps lan.
The gatling load test is run on another host, running 1000 concurrent users.

--- 
### Indexing Load Test Output ###

    ================================================================================
    ---- Global Information --------------------------------------------------------
    > numberOfRequests                                 2346426 (OK=2346426 KO=0     )
    > minResponseTime                                        0 (OK=0      KO=-     )
    > maxResponseTime                                     1570 (OK=1570   KO=-     )
    > meanResponseTime                                       3 (OK=3      KO=-     )
    > stdDeviation                                          29 (OK=29     KO=-     )
    > percentiles1                                          10 (OK=10     KO=-     )
    > percentiles2                                          10 (OK=10     KO=-     )
    > meanNumberOfRequestsPerSecond                       3351 (OK=3351   KO=-     )
    ---- Response Time Distribution ------------------------------------------------
    > t < 800 ms                                       2345586 ( 99%)
    > 800 ms < t < 1200 ms                                  82 (  0%)
    > t > 1200 ms                                          758 (  0%)
    > failed                                                 0 (  0%)
    ================================================================================
----
### Searching Load Test Output ###

    ================================================================================
    ---- Global Information --------------------------------------------------------
    > numberOfRequests                                 3350140 (OK=3350140 KO=0     )
    > minResponseTime                                        0 (OK=0      KO=-     )
    > maxResponseTime                                     4310 (OK=4310   KO=-     )
    > meanResponseTime                                      76 (OK=76     KO=-     )
    > stdDeviation                                         110 (OK=110    KO=-     )
    > percentiles1                                         140 (OK=140    KO=-     )
    > percentiles2                                         470 (OK=470    KO=-     )
    > meanNumberOfRequestsPerSecond                       4785 (OK=4785   KO=-     )
    ---- Response Time Distribution ------------------------------------------------
    > t < 800 ms                                       3335529 ( 99%)
    > 800 ms < t < 1200 ms                               11950 (  0%)
    > t > 1200 ms                                         2661 (  0%)
    > failed                                                 0 (  0%)
    ================================================================================

