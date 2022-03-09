package decorator;

/**
 * 功能描述:
 *
 * @Class ConcreteDecorator2
 * @Author ZYC
 * @Date 2021/4/22 11:42
 * @Version 1.0
 **/
public class ConcreteDecorator2 extends Decorator {
    public ConcreteDecorator2(Component component){
        super(component);
    }

    @Override
    public void operation() {
        super.operation();
        //自定义方法
        this.addFunction();
    }

    public void addFunction(){
        System.out.println("我是具体装饰方法2的增加方法");
    }
}
