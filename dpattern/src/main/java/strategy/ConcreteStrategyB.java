package strategy;

/**
 * 功能描述:
 *
 * @Class ConcreteStrategyB
 * @Author ZYC
 * @Date 2021/3/16 14:46
 * @Version 1.0
 **/
public class ConcreteStrategyB implements IStrategy {
    @Override
    public void algorithm() {
        System.out.println("这是算法A");
    }
}
