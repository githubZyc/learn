package decorator;

/**
 * 功能描述:
 * 抽象装饰（Decorator）角色：实现抽象构件，并包含具体构件的实例，可以通过其子类扩展具体构件的功能。
 * @Class Decorator
 * @Author ZYC
 * @Date 2021/4/22 11:37
 * @Version 1.0
 **/
public class Decorator implements Component{
    private Component component;

    Decorator(){}

    Decorator(Component component){
        this.component = component;
    }

    @Override
    public void operation() {
        //调用原有实现
        component.operation();
    }
}
