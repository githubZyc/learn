package factory.factorymethod.pay;

/**
 * 功能描述:
 * 阿里支付流程
 * @Class AliPayClient
 * @Author ZYC
 * @Date 2021/4/1 9:44
 * @Version 1.0
 **/
public class AliPayClient implements PayClient {
    @Override
    public String preparePay() {
        System.out.println("支付宝不需要预支付");
        return null;
    }

    @Override
    public String pay() {
        System.out.println("支付宝支付成功");
        return null;
    }

    @Override
    public String refund() {
        System.out.println("支付宝支付退款成功");
        return null;
    }
}
