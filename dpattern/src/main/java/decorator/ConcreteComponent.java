package decorator;

/**
 * 功能描述:
 * 具体构件（ConcreteComponent）角色：实现抽象构件，通过装饰角色为其添加一些职责
 * @Class ConcreteComponent
 * @Author ZYC
 * @Date 2021/4/22 11:36
 * @Version 1.0
 **/
public class ConcreteComponent implements Component{
    public ConcreteComponent() {
        System.out.println("创建具体构件角色");
    }
    @Override
    public void operation() {
        System.out.println("调用具体构件角色的方法operation()");
    }
}
