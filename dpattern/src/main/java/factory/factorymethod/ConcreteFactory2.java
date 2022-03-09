package factory.factorymethod;

/**
 * 功能描述:
 * 具体工厂（ConcreteFactory）：主要是实现抽象工厂中的抽象方法，完成具体产品的创建。
 * @Class ConcreteFactory2
 * @Author ZYC
 * @Date 2021/4/1 9:27
 * @Version 1.0
 **/
public class ConcreteFactory2 implements AbstractFactory {
    @Override
    public Product newProduct() {
        System.out.println("具体工厂2生成-->具体产品2...");
        return new ConcreteProduct1();
    }
}
