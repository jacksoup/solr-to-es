#SOLR-TO-ES介绍
提供了一套solr到elasticsearch的语法翻译引擎，兼容现有的solr语法。同时提供了基于注解的ORM功能，
自动完成实体类对象与elasticsearch结果之间的转换。
你只要进行简单的配置，系统便会会自动完成索引、type/mapping的创建工作。
同时提供了比较常用的底层api。
比如，通过这个工具，你可以用下面这条solr查询语句在elasticsearch上进行搜索：
```
q=name:"季度"~1 AND type:Q&fq=tgsType:2&start=0&rows=5&sort=name+desc,code+asc&fl=name,code,logicId,pinyin&hl=true&hl.fl=name,code
&hl.simple.pre=<em>&hl.simple.post=</em>
```

#工具特点
- 基于注解自动创建index、type/mapping
- 兼容solr语法
- 提供了ORM功能，实体类和"表"之间自动转换
- 提供了丰富的底层api
- 使用简单，只需简单配置

#如何使用


**1.进行相关环境设置：**
请在你的classpath目录新建elasticsearch.properties文件，内容参考如下：

> elasticsearch集群名称
> cluster.name = es_nrs_log
> elasticsearch集群中的主机地址列表（多个主机，以","分隔）
> hosts = 183.136.163.88,183.136.162.194,183.136.162.71
> elasticsearch客户端连接端口号（默认9300）
> client.port = 9300
> 每次运行前是否进行检测（检测相关的索引和type/mapping是否存在，不存在则根据实体类的注解自动生成）
> 第一次运行可以设置为true,以后更改为false
> isCheck = true
> 设置实体类所在的包路径
> models.package.dir = org.cnfire.elasticsearch.model


**2.定义实体类:**
eg:

    @Document(index = "school", type = "student", settings = "school-settings.json")
    public class Student {
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
        @Field(type = FieldType.String)
        boolean isGraduated;
        @Field(analyzer = "ik")
        String description;

    相关的setter和getter省略

现在，你已经定义了一个实体类，其描述的索引为school，相应应的type为student

**注：**相关注解的详细配置参考说明，会在接下来的时间里提供说明文档
3.现在你可以进行相关操作了，参考如下：

    public class TestAccessor {
        Class<Student> clazz = Student.class;
        IAccessor accessor = Beans.getAccessor();

        /*添加单个对象*/
        @Test public boolean add(){
            Student model = new Student();
            model.setId("1001");
            model.setName("jack");
            model.setAge(30);
            model.setSex("male");
            model.setScore(99.5);
            model.setIsGraduated(true);
            model.setDescription("jack is a good boy...");
            return accessor.add(model);
        }

        /*添加对象集合*/
        @Test public boolean testAddList(){
            Student model1 = new Student();
            model1.setName("kobe");
            Student model2 = new Student();
            model2.setName("james");
            Student model3 = new Student();
            model3.setName("jodarn");
            List<Student> list= new ArrayList<Student>();
            list.add(model1);
            list.add(model2);
            list.add(model3);
            return accessor.add(list);
        }

        /*根据ID获取记录*/
        @Test public void get(){
            System.out.println(accessor.get(clazz,"1001"));
        }

        /*根据查询条件统计结果个数*/
        @Test public void count(){
            String params = "q=sex:male AND isGraduated:true";
            System.out.println(accessor.count(clazz, params));
        }

        /*根据条件进行搜索*/
        @Test public void search(){
            String params = "q=sex:male AND isGraduated:true&start=0&rows=15&sort=name+desc,age+asc&fl=name,score";
            System.out.println(accessor.search(clazz, params));
        }

        /*根据条件进行高亮查询*/
        @Test public void searchWithHL(){
            String params = "q=sex:male AND isGraduated:true&start=0&rows=15&sort=name+desc,age+asc&fl=name,score" +
                    "&hl=true&hl.fl=name&hl.simple.pre=<em>&hl.simple.post=</em>";
            System.out.println(accessor.search(clazz, params));
        }

        /*根据ID删除一条记录*/
        @Test public boolean deleteRecord(){
            return accessor.delete("ID1001",clazz);
        }

        /*根据查询条件删除多条记录*/
        @Test public boolean deleteAll(){
            String params = "q=sex:male AND isGraduated:true";
            return accessor.delete(clazz,params);
        }

        /*根据输入的索引名称创建相应索引*/
        @Test public boolean createIndex(){
            return accessor.createIndex("testIndexName");
        }

        /*根据实体类自动解析注解，获取相应settings，创建相应索引*/
        @Test public boolean createIndexWithSettings(){
            return accessor.createIndex(clazz);
        }

        /*根据索引名删除相应索引*/
        @Test public boolean deleteIndex(){
            return accessor.deleteIndex("school");
        }

        /*删除相应类型*/
        @Test public boolean deleteType(){
            /*根据索引名和类型名删除相应类型*/
            return accessor.deleteType("school","student");
            // OR
            /*根据实体类删除类型*/
            //accessor.deleteType(clazz);
        }

        /*根据实体类创建相关表对应的mapping*/
        @Test public void createMapping(){
            accessor.createMapping(clazz);
        }

        /*测试是否存在指定的索引*/
        @Test public boolean hasIndex(){
            return accessor.hasIndex("school");
        }

        /*测试是否存在指定的mapping/类型*/
        @Test public boolean hasMapping(){
            /*根据实体类判断*/
            return accessor.hasMapping(clazz);
            /*根据索引名和类型名判断*/
    //        return accessor.hasMapping("school","student");
        }
    }


ok,当你运行程序时候，系统会自动检测elasticsearch中相关的index和type/mapping是否存在，如果不存在则会根据注解自动创建

**注：**本系统所提供的查询相关API暂只能使用标准的Lucene查询语法，比如你要显示地使用match_all,term等，其实都可以通过标准的Lucene语法进行查询
