import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 功能描述:
 *
 * @Class FlatMapClass
 * @Author ZYC
 * @Date 2021/4/2 14:11
 * @Version 1.0
 **/
public class FlatMapClass {
    static class StudentGroup{
        List<Student> lst;
        String groupId;
        String groupName;

        public List<Student> getLst() {
            return lst;
        }

        public void setLst(List<Student> lst) {
            this.lst = lst;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }
    static class Student{
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    public static void main(String[] args) {
        List<Student> studentList1 = new ArrayList<>(2);
        Student stu1 = new Student();
        stu1.setAge(18);
        stu1.setName("李小牧");
        studentList1.add(stu1);

        Student stu2 = new Student();
        stu1.setAge(18);
        stu1.setName("王晓涵");
        studentList1.add(stu2);


        Student stu3 = new Student();
        stu1.setAge(18);
        stu1.setName("郑悦");
        studentList1.add(stu3);

        List<Student> studentList2 = new ArrayList<>(2);
        Student stu4 = new Student();
        stu1.setAge(18);
        stu1.setName("郑高丽");
        studentList2.add(stu4);

        Student stu5 = new Student();
        stu1.setAge(18);
        stu1.setName("陈咪");
        studentList2.add(stu5);

        StudentGroup studentGroup = new StudentGroup();
        studentGroup.setGroupId("1");
        studentGroup.setGroupName("第一队");
        studentGroup.setLst(studentList1);

        StudentGroup studentGroup2 = new StudentGroup();
        studentGroup2.setGroupId("2");
        studentGroup2.setGroupName("第二队");
        studentGroup2.setLst(studentList2);

        List<StudentGroup> result = new ArrayList<>(2);
        result.add(studentGroup);
        result.add(studentGroup2);

//        List<List<Student>> collect = result.stream().map(StudentGroup::getLst).collect(Collectors.toList());
        Stream<Student> studentStream = result.stream().flatMap((studentGroup1 -> studentGroup1.getLst().stream()));
    }
}
