package decorator;

/**
 * 功能描述:
 *
 * @Class DecoratorPattern
 * @Author ZYC
 * @Date 2021/4/22 11:47
 * @Version 1.0
 **/
public class DecoratorPattern {
    public static void main(String[] args) {
        //调用接口的具体实现
        Component component = new ConcreteComponent();
        component.operation();
        System.out.println("---------------------------------");

        //装饰接口1
        Component component1 = new ConcreteDecorator1(component);
        component1.operation();
        //装饰接口1
        Component component2 = new ConcreteDecorator1(component);
        component2.operation();
    }
}
