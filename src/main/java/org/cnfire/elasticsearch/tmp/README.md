#instruction
Provides the solr to elasticsearch grammar translation engine, compatible with existing solr grammar,
you can use elasticsearch with solr query syntax, online search business about solr can be seamless
switching to elasticsearch.


#How to use
The following to introduce the use of simple, behind will do detailed instructions
please reference package Examples

-step1:configure some environment params
include elasticsearch.properties and settings.xml
configure elasticsearch.properties eg:
#your es cluster name
cluster.name = es_nrs_log
#your hosts,if have two or up,please
hosts = 183.136.163.88
#es client port default 9300
client.port = 9300
#es web client default 9200
web.port = 9200
#highlight tags when you set higtlight
highlight.pre.tags = <span style=\"color:red\">
highlight.post.tags = </span>
#
isRedo = false
isCheck = true
models.package = org.lzo.elasticsearch.model
start = 0
rows = 10

- step1:define your model like org.shms.elasticsearch.model.Model



- step2:get a accessor to operate es by useing Beans.getAccessor()



- step3:now you can operate elasticsearch by solr query syntax like org.shms.elasticsearch.Example.Example

eg:

    String params = "q=name:季度&fq=tgsType:2&start=0&rows=5&sort=name+desc,code+asc&" +
    "fl=name,code,logicId,pinyin&hl=true&hl.fl=name,code" +
    "&hl.simple.pre=<em>&hl.simple.post=</em>";
    List<Model> list = accessor.search(clazz,params);
    for (Model model : list){
    System.out.println(model);
    }
    


*consummating*............

