package single;

/**
 * 功能描述:
 * 懒汉式单例
 * @Class President
 * @Author ZYC
 * @Date 2021/3/31 11:23
 * @Version 1.0
 **/
public class President {
    private static volatile President president = null;

    private String name;
    private Integer age;

    private President(){}

    private President(String name,Integer age){
        this.name = name;
        this.age = age;
    }

    public static synchronized President getPresident(){
        if(president == null){
            System.out.println("还没领导人！实例化一个");
            //只能是一个领导人
            president = new President("特朗普",65);
        }
        System.out.println("已有领导人。");
        return president;
    }

    public static void main(String[] args) {
        President president = getPresident();

        System.out.println("第一位领导人：" + president.toString());

        President president1 = getPresident();

        System.out.println("第二位领导人：" + president.toString());

        System.out.println("好像" +(president.equals(president1)  ? "是" : "不是") + "同一个人");
    }

    @Override
    public String toString() {
        return "President{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
