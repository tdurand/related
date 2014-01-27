# ![Relateit](./relateitlogo.png)
## (**\*BETA\***)


**relateit** is a simple and easy way to relate one item to several others.  Once several items are related, you can enquire:  "For this item, what are the most frequently related items."

An example use case of this functionality is on a web site to associate the items the people are purchasing.  If a person purchases the books 'Java Performance Tuning', 'Akka Concurrency' and 'Java concurrency in practice' at the same time.  When a second user is browsing 'Java Performance Tuning', you can present that user with related items that this book is most frequently purchased with.  

This application has 3 parts to it:

* A Indexing Web Application
* A Searching Web Application
* [Elasticsearch](http://www.elasticsearch.org/ "Elasticsearch") backend

The indexing and searching components (web applications) make use of the [Disruptor](https://github.com/LMAX-Exchange/disruptor "Disruptor") library.  

The search technology Elasticsearch provides the storage, and searching mechanism for providing the related product search.

The Indexing and Searching components do not need to directly be used.  In other words you can just post data in the relevant format into elasticsearch, and then perform searching directly against elasticsearch to obtain the most frequently related items.   However, it is the Indexing and Searching components that provide a means of batching indexing and searching requests that are being send to elasticsearch.


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

    curl -v -N "http://10.0.1.29:8080/searching/frequentlyrelatedto/1?type=torch&channel=uk" | python -mjson.tool

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

## More Indexing##  

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

### How Many Properties Can I Have for Related Item ###

The answer to this question is that you 

## Elasticsearch ##

As mentioned above, the related item data is stored in elasticsearch and the ability to find the frequently related items, for an item, is provided by that of elasticsearch and its faceting.  

The indexing and searching web applications use the elasticsearch java library.  

The means by which the indexing and searching applications talk to elastic is by using the elasticsearch binary transport protocol.  Meaning, the version of the client embedded within the web applications (indexing and searching), *MUST* match that of the elasticsearch server.  Also, the version of JAVA on the client and the server *MUST* be the same.

* Current embedded elasticsearch version is: **0.90.9**

By default both applications use the Transport protocol to connect to elasticsearch with sniffing enabled:

    client.transport.sniff : true

(Sniffing means that you can specify only a couple of hosts, and the client will glen information on the rest of the cluster through those nodes).

The reason behind no support for HTTP endpoint is just to focus on using the most performant client option, which is that of the transport client.  HTTP support is on the list of things to enable, but it would require either extra configuration at your side (i.e. a load balancer), or for the application to provide a simple round robin implementation to round robin request over a list of nodes.  So at the moment only the "**node**", or "**transport**" option are available.  With the default being that of **transport**.


## Elasticsearch Connection Configuration ##

The defaults for indexing and searching have been set based on a JVM the is running 1GB with 128m of PermGen (The specific configuration for these JVM Parameters can be found below).

At minimum the only configuration required is the connection details for your elasticsearch installation:

    -Drelated-item.elastic.search.transport.hosts=10.0.1.19:9300

This can be a comma separated list of hosts:

    -Drelated-item.elastic.search.transport.hosts=10.0.1.19:9300,10.0.1.29:9300

By default the application uses the TRANSPORT client to connect to elastic search.  If you only specify one host, but you have 2 nodes in your elasticsearch cluster, the transport client is enabled by default to sniff (ask the node for information about other nodes in the cluster), and obtain a list of other nodes to connect to.

When the indexing and searching applications talk to elasticsearch they search for documents within the index "relateditems-YYYY-MM-DD" for the document type "related".  As previously mentioned the defined properties require for the "related" type are: 

* id 
* related-with
* date

When indexing documents in elasticsearch, if a type (i.e. "related") does not have an associated mapping then a dynamic mapping of a document's json properties are created.  Which may or may not be what is required.  As a result, you should define a mapping for the type.  The mapping for the related type should at minimum be the following:

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

### JVM Options and Configuration Defaults #

The default configuration for indexing and searching are based on a 1GB heap (-Xmx1024m -Xms1024m) configuration.  

The application configuration

The specific recommended (tested against) JVM options for searching and indexing are listed below (jdk7 - the following options *WILL NOT* work on jdk6).  The JVM options slightly differ between searching and indexing.  The common options are listed and then the differences listed:

### Common options

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

Below shows the heap configuration for indexing and search.  The difference between the two is that of the eden space.

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




## Load Testing ##

Below show the load testing results from indexing and searching tests performed against the application during development and testing.  You will obviously have different results based on your representative indexing data and searches.  

The load tests results are from running a 1GB heap and running indexing and searching completely independently of each other.

Elasticsearch version 0.90.9 is running on 2 hosts:

* Mac mini 2.3 GHz Core i5 (I5-2415M)
* macbook pro 17" 2.5 GHz Core i7 (I7-2860QM) 

The indexing and searching applications are running on:

* Dell poweredge t420, 2 cpu Intel(R) Xeon(R) CPU E5-2407 2.20GHz.
* tomcat 7u42, NIO connector.
* centos 6.5

The connection between the dell t420 and the macbook pro is wifi 5g, and to mac mini 100mbps lan.

The gatling load test is run on another host, running 1000 concurrent users.

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

Searching load testing results:

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

#Indexing info


    HOST=localhost





'''

Elastic config
'''
cluster.name: relateditems

# Search pool
threadpool.search.type: fixed
threadpool.search.size: 20
threadpool.search.queue_size: 1000000

# Bulk pool
threadpool.bulk.type: fixed
threadpool.bulk.size: 25
threadpool.bulk.queue_size: 1000000

threadpool.get.type: fixed
threadpool.get.size: 1
threadpool.get.queue_size: 1


# Index pool
threadpool.index.type: fixed
threadpool.index.size: 20
threadpool.index.queue_size: 1000000
'''




Sample indexing json

'''
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
      }
   ]
}
'''


Sample curl request for indexing:


    curl -H"Content-Type:text/json" -XPOST -v http://localhost:8080/indexing/index -d         '{ "channel" : "uk", "site" : "amazon", "date" : "2013-05-22T20:31:35", "items" : [ { "id" :     "111","type":"coat"}, { "id" : "123","type":"socks"}, { "id" : "23334","type":"button"} ]  }'


Sample indexing configuration params:

'''
-Drelated-product.wait.strategy=busy -Drelated-product.size.of.incoming.request.queue=131072 -Drelated-product.number.of.indexing.request.processors=8 -Drelated-product.index.batch.size=125 -Drelated-product.elastic.search.transport.hosts=10.0.1.19:9300
'''


---

Sample search request

    curl -v -N http://10.0.1.29:8080/searching/frequentlyrelatedto/8855?channel=uk

    curl -v -N http://localhost:8080/searching/frequentlyrelatedto/123?channel=uk



in elastic this would be:

'''
curl -XPOST http://macmini:9200/relateditemss*/relateditem/_search -d '
{
  "query" :
        {
            "bool" : {
                "must" : [
                    {"field" : {"id" : "338906"} },
                    {"field" : {"channel" : "uk"} },
                    {"field" : {"site" : "amazon"} }
                ]
            }
        },
        "facets" : {
            "frequently-related-with" : {
                "terms" : {"field" : "related-with", "size" : 5 }
            }
        },
        "size":0
}
'
'''