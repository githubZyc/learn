package adapter.obj;

/**
 * 功能描述:
 * 它是被访问和适配的现存组件库中的组件接口。
 * @Class Adaptee
 * @Author ZYC
 * @Date 2021/4/2 16:05
 * @Version 1.0
 **/
public class Adaptee {
    void specificRequest(){
        System.out.println("适配者中的业务代码被调用！");
    }
}
