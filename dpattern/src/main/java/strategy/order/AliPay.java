package strategy.order;

/**
 * 功能描述:
 * 支付宝支付
 * @Class AliPay
 * @Author ZYC
 * @Date 2021/3/16 15:37
 * @Version 1.0
 **/
public class AliPay implements Payment {
    @Override
    public String getName() {
        return "支付宝";
    }

    @Override
    public double queryBalance(String uid) {
        return 900;
    }
}
