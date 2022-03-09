package factory.factorymethod.pay;

/**
 * 功能描述:
 *
 * @Class WechatPayFactory
 * @Author ZYC
 * @Date 2021/4/1 9:42
 * @Version 1.0
 **/
public class WechatPayFactory implements AbstractPayFactory {
    @Override
    public PayClient newPayClient() {
        return new WechatPayClient();
    }
}
