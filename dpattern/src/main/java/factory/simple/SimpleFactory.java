package factory.simple;

/**
 * 功能描述:
 * 简单工厂
 * @Class SimpleFactory
 * @Author ZYC
 * @Date 2021/3/31 19:05
 * @Version 1.0
 **/
public class SimpleFactory {
    Product makeProduct(int cNumber){
        switch (cNumber){
            case 1:
                return new ConcreteProduct1();
            case 2:
                return new ConcreteProduct2();
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        SimpleFactory simpleFactory = new SimpleFactory();
        Product product = simpleFactory.makeProduct(1);
        System.out.println();
    }
}
