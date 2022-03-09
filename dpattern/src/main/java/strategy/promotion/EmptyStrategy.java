package strategy.promotion;

/**
 * 功能描述:
 *
 * @Class EmptyStrategy
 * @Author ZYC
 * @Date 2021/3/16 15:00
 * @Version 1.0
 **/
public class EmptyStrategy implements IPromotionStrategy {
    @Override
    public void doPromotion() {
        System.out.println("原价购买，无优惠");
    }
}
