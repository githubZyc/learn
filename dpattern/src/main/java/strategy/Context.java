package strategy;

/**
 * 功能描述:
 * 上下文对象
 * @Class Context
 * @Author ZYC
 * @Date 2021/3/16 14:46
 * @Version 1.0
 **/
public class Context {
    private IStrategy strategy;

    public Context(IStrategy strategy) {
        this.strategy = strategy;
    }
    //由构造器中传参实现子类类型，来决定选择哪一种算法
    public void algorithm(){
        this.strategy.algorithm();
    }
}
