package strategy;

/**
 * 功能描述:
 * 具体策略子类A
 * @Class ConcreteStrategyA
 * @Author ZYC
 * @Date 2021/3/16 14:45
 * @Version 1.0
 **/
public class ConcreteStrategyA implements IStrategy {
    @Override
    public void algorithm() {
        System.out.println("这是算法A");
    }
}
