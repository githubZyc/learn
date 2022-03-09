package strategy.order;

/**
 * 功能描述:
 *
 * @Class WechatPay
 * @Author ZYC
 * @Date 2021/3/16 15:40
 * @Version 1.0
 **/
public class WechatPay implements Payment {
    @Override
    public String getName() {
        return "微信支付";
    }

    @Override
    public double queryBalance(String uid) {
        return 500;
    }
}
