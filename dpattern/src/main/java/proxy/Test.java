package proxy;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/4/1 16:45
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        Proxy proxy = new Proxy(new RealSubject());
        String s = proxy.showSubject();
        System.out.println("代理方法执行后的结果：" + s);
    }
}
