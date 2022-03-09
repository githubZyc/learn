package prototype.citation;

/**
 * 功能描述:
 * 奖状类
 * @Class Citation
 * @Author ZYC
 * @Date 2021/3/31 14:23
 * @Version 1.0
 **/
public class Citation implements Cloneable{
    //获奖人
    private String name;
    //具体通用话术
    private String info;
    //大学
    private String college;

    public Citation(String name, String info, String college) {
        this.name = name;
        this.info = info;
        this.college = college;
        System.out.println("创建奖状成功。" + this.toString());
    }

    @Override
    public String toString() {
        return "Citation{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", college='" + college + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        System.out.println("奖状拷贝成功！");
        return super.clone();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
