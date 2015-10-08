import org.cnfire.elasticsearch.accessor.IAccessor;
import org.cnfire.elasticsearch.util.Beans;
import org.testng.annotations.Test;
import org.xxx.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack.zhu on 15-9-22.
 */
public class TestAccessor {
    Class<Student> clazz = Student.class;
    IAccessor accessor = Beans.getAccessor();


    /*添加单个对象*/
    @Test public boolean add(){
        Student model = new Student();
        model.setId("1002");
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

    /*根据实体类*/
    public static void main(String[] args) {
        TestAccessor example = new TestAccessor();
        example.add();
//        example.get();
//        example.count();
        example.search();
//        example.deleteIndex();
    }
}
