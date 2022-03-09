package factory.factorymethod;

/**
 * 功能描述:
 * 抽象工厂：提供了创建产品的接口，调用者通过它访问具体工厂的工厂方法 newProduct() 来创建产品。
 * @Class AbstractFactory
 * @Author ZYC
 * @Date 2021/4/1 9:21
 * @Version 1.0
 **/
public interface AbstractFactory {
    Product newProduct();
}
