#SOLR-TO-ES介绍
提供了solr到elasticsearch的语法翻译引擎，完全兼容现有的solr语法，你可以通过这个工具使用solr的查询语法进行基于elasticsearch的搜索。
比如，你可以用下面这条solr查询语句在elasticsearch上进行搜索
```
q=name:"季度"~1 AND type:Q&fq=tgsType:2&start=0&rows=5&sort=name+desc,code+asc&fl=name,code,logicId,pinyin&hl=true&hl.fl=name,code
&hl.simple.pre=<em>&hl.simple.post=</em>
```
由于本人所在的公司的搜索业务由solr升级到elasticsearch，为了实现兼容现有搜索业务层，故该开发了这个工具。

#工具特点
- 基于注解自动创建index、type，省去了手动配置操作
- 兼容solr语法
- 提供了丰富的操作api
- 提供了ORM功能，实体类和"表"之间自动转换
- 使用简单，只需简单配置即可使用

#如何使用


**1.进行相关环境设置：**
eg:
设置集群名称
cluster.name = es_nrs_log
设置主机地址
hosts = 183.136.163.88
、、、、、
**2.定义实体类:**
eg:

    @Document(index = "school", type = "student", init = false)
    public class Model {
    @ID
    String id;
    @Field(type = FieldType.String, index = FieldIndex.analyzed, stored = true, analyzer = "ik")
    String name;
    @Field
    String sex;
    @Field(type = FieldType.Integer)
    int age;
    @Field(type = FieldType.Float)
    double score;
    @Field(analyzer = "ik")
    String description;

    相关的setter和getter省略

现在，你已经定义了一个实体类，所在的索引目录为school，对应的type为student
3.现在你可以进行相关操作了
eg:
public class Example {
    //实体类的calss对象
    Class<Model> clazz = Model.class;
    //通过Beans.getAccessor()获得一个访问对象
    IAccessor accessor = Beans.getAccessor();

    //添加一个对象
    public void add(){
        Model model = new Model();
        model.setId("1001");
        model.setName("jack");
        model.setAge(30);
        model.setSex("male");
        model.setScore(99.5);
        model.setIsGraduated(true);
        model.setDescription("jack is a good boy");
        accessor.add(model);
    }

    //根据ID查找一个对象
    public void get(){
        System.out.println(accessor.get(clazz,"1001"));
    }

    //根据查询条件统计结果个数
    public void count(){
        String params = "q=name:*";
        System.out.println(accessor.count(clazz, params));
    }

    //进行搜索
    public void search(){
        String params = "q=name:jack&hl=true&hl.fl=name";
        System.out.println(accessor.search(clazz, params));
    }
    }

ok,当你运行程序时候，系统会自动检测相关的index和type是否存在，如果不存在则会根据注解自动进行配置和创建，你在调用接口时，需要传递一个相应实体类的class对象，
系统会自动完成相应的操作并进行"表"与实体类的自动转换

