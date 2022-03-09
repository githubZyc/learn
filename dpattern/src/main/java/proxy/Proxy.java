package proxy;

/**
 * 功能描述:
 * 提供了与真实主题相同的接口，其内部含有对真实主题的引用，
 * 它可以访问、控制或扩展真实主题的功能。
 * @Class Proxy
 * @Author ZYC
 * @Date 2021/4/1 16:40
 * @Version 1.0
 **/
public class Proxy implements Subject{
    RealSubject realSubject;

    Proxy(RealSubject realSubject){
        this.realSubject = realSubject;
    }
    @Override
    public String showSubject() {
        System.out.println("执行代理方法");
        String s = realSubject.showSubject();
        System.out.println("目标方法执行完成");
        return s + "proxy";
    }
}
