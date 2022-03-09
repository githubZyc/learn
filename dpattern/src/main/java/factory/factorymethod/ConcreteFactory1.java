package factory.factorymethod;

/**
 * 功能描述:
 * 具体工厂（ConcreteFactory）：主要是实现抽象工厂中的抽象方法，完成具体产品的创建。
 * @Class ConcreteFactory1
 * @Author ZYC
 * @Date 2021/4/1 9:26
 * @Version 1.0
 **/
public class ConcreteFactory1 implements AbstractFactory {

    @Override
    public Product newProduct() {
        System.out.println("具体工厂1生成-->具体产品1...");
        return new ConcreteProduct();
    }
}
