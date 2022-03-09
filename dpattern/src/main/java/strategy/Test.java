package strategy;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/3/16 14:47
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        //通过客户端选择具体策略
        IStrategy concreteStrategyA = new ConcreteStrategyA();
        Context context = new Context(concreteStrategyA);
        context.algorithm();
    }
}
