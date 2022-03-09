package factory.factorymethod;

/**
 * 功能描述:
 * 具体产品（ConcreteProduct）：实现了抽象产品角色所定义的接口，由具体工厂来创建，它同具体工厂之间一一对应。
 * @Class ConcreteProduct
 * @Author ZYC
 * @Date 2021/4/1 9:28
 * @Version 1.0
 **/
public class ConcreteProduct implements Product {
    @Override
    public void show() {
        System.out.println("具体产品1显示...");
    }
}
