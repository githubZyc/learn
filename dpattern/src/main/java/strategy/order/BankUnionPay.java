package strategy.order;

/**
 * 功能描述:
 *
 * @Class BankUnionPay
 * @Author ZYC
 * @Date 2021/3/16 15:41
 * @Version 1.0
 **/
public class BankUnionPay implements Payment{
    @Override
    public String getName() {
        return "银联支付";
    }

    @Override
    public double queryBalance(String uid) {
        return 120;
    }
}
