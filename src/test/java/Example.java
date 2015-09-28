import org.cnfire.elasticsearch.accessor.IAccessor;
import model.Model;
import org.cnfire.elasticsearch.util.Beans;

/**
 * Created by jack.zhu on 15-9-22.
 */
public class Example {
    Class<Model> clazz = Model.class;
    IAccessor accessor = Beans.getAccessor();

    public void add(){
        Model model = new Model();
        model.setId("1001");
        model.setName("jack");
        model.setAge(30);
        model.setSex("male");
        model.setScore(99.5);
        model.setIsGraduated(true);
        model.setDescription("jack is a good boy...");
        accessor.add(model);
    }

    public void get(){
        System.out.println(accessor.get(clazz,"1001"));
    }

    public void count(){
        String params = "q=name:*";
        System.out.println(accessor.count(clazz, params));
    }

    public void search(){
        String params = "q=name:jack&hl=true&hl.fl=name";
        System.out.println(accessor.search(clazz, params));
    }

    public void deleteIndex(){
        accessor.deleteIndex("school");
    }
    public static void main(String[] args) {
        Example example = new Example();
        example.add();
//        example.get();
//        example.count();
        example.search();
//        example.deleteIndex();
    }
}
