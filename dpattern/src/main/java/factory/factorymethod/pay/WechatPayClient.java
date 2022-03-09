package factory.factorymethod.pay;

/**
 * 功能描述:
 * 微信支付流程
 * @Class WechatPayClient
 * @Author ZYC
 * @Date 2021/4/1 9:46
 * @Version 1.0
 **/
public class WechatPayClient implements PayClient {
    @Override
    public String preparePay() {
        System.out.println("微信支付预支付成功");
        return null;
    }

    @Override
    public String pay() {
        System.out.println("微信支付成功");
        return null;
    }

    @Override
    public String refund() {
        System.out.println("微信退款成功");
        return null;
    }
}
