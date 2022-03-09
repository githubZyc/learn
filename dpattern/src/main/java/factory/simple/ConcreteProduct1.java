package factory.simple;

/**
 * 功能描述:
 *
 * @Class ConcreteProduct1
 * @Author ZYC
 * @Date 2021/3/31 19:09
 * @Version 1.0
 **/
public class ConcreteProduct1 implements Product {
    @Override
    public void show() {
        System.out.println("我是：ConcreteProduct1");
    }
}
