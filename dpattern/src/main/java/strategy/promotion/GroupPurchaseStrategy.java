package strategy.promotion;

/**
 * 功能描述:
 * 团购促销
 * @Class GroupPurchaseStrategy
 * @Author ZYC
 * @Date 2021/3/16 14:57
 * @Version 1.0
 **/
public class GroupPurchaseStrategy implements IPromotionStrategy {
    @Override
    public void doPromotion() {
        System.out.println("5人成团，可以优惠");
    }
}
