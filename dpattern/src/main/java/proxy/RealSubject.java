package proxy;

/**
 * 功能描述:
 * 实现了抽象主题中的具体业务，是代理对象所代表的真实对象，是最终要引用的对象。
 * @Class RealSubject
 * @Author ZYC
 * @Date 2021/4/1 16:39
 * @Version 1.0
 **/
public class RealSubject implements Subject{
    @Override
    public String showSubject() {
        System.out.println("目标真实方法被执行");
        return "real";
    }
}
