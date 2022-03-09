package decorator;

/**
 * 功能描述:
 * 具体装饰（ConcreteDecorator）角色：实现抽象装饰的相关方法，并给具体构件对象添加附加的责任。
 * @Class ConcreteDecorator1
 * @Author ZYC
 * @Date 2021/4/22 11:40
 * @Version 1.0
 **/
public class ConcreteDecorator1 extends Decorator {

    public ConcreteDecorator1(Component component){
        super(component);
    }

    @Override
    public void operation() {
        super.operation();
        //自定义方法
        this.addFunction();
    }

    public void addFunction(){
        System.out.println("为具体构件角色增加额外的功能addedFunction()");
    }
}
