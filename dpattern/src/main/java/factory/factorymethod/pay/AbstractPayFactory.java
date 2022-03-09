package factory.factorymethod.pay;

/**
 * 功能描述:
 * 定义生产支付类型的工厂
 * @Class AbstractPayFactory
 * @Author ZYC
 * @Date 2021/4/1 9:38
 * @Version 1.0
 **/
public interface AbstractPayFactory {
    PayClient newPayClient();
}
