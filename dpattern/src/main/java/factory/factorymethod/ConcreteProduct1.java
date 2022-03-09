package factory.factorymethod;

/**
 * 功能描述:
 *
 * @Class ConcreteProduct1
 * @Author ZYC
 * @Date 2021/4/1 9:29
 * @Version 1.0
 **/
public class ConcreteProduct1 implements Product {
    @Override
    public void show() {
        System.out.println("具体产品2显示...");
    }
}
