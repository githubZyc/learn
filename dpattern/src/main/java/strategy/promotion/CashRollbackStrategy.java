package strategy.promotion;

/**
 * 功能描述:
 * 返现促销
 * @Class CashRollbackStrategy
 * @Author ZYC
 * @Date 2021/3/16 14:58
 * @Version 1.0
 **/
public class CashRollbackStrategy implements IPromotionStrategy {
    @Override
    public void doPromotion() {
        System.out.println("返现，直接打款到支付宝帐号");
    }
}
