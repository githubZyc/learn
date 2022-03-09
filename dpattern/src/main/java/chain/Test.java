package chain;

/**
 * 功能描述:
 * 责任链客户端配置链条执行顺序，于处理逻辑
 * @Class Test
 * @Author ZYC
 * @Date 2021/4/22 17:28
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        //组装链
        Leader  classAdviser = new ClassAdviser();
        Leader  departmentHead = new DepartmentHead();
        classAdviser.setNextLeader(departmentHead);
        classAdviser.handleRequest(9);
    }
}
