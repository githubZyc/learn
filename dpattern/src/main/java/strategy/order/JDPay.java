package strategy.order;

/**
 * 功能描述:
 *
 * @Class JDPay
 * @Author ZYC
 * @Date 2021/3/16 15:40
 * @Version 1.0
 **/
public class JDPay implements Payment {
    @Override
    public String getName() {
        return "支付宝";
    }

    @Override
    public double queryBalance(String uid) {
        return 900;
    }
}
