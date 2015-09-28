package model;

import org.cnfire.elasticsearch.annotations.Document;
import org.cnfire.elasticsearch.annotations.Field;
import org.cnfire.elasticsearch.annotations.ID;
import org.cnfire.elasticsearch.data.FieldIndex;
import org.cnfire.elasticsearch.data.FieldType;

/**
 * Created by jack.zhu on 15-9-22.
 */
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

    public String getId() {
        return id;
    }

    public void setId(String code) {
        this.id = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descib) {
        this.description = descib;
    }

    public boolean getIsGraduated() {
        return isGraduated;
    }

    public void setIsGraduated(boolean isGraduated) {
        this.isGraduated = isGraduated;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", score=" + score +
                ", isGraduated='" + isGraduated + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
