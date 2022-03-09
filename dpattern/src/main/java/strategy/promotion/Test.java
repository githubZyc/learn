package strategy.promotion;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/3/16 15:07
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        String promotionKey = "COUPON";
        IPromotionStrategy strategy = PromotionStrategyFactory.getPromotionStrategy(promotionKey);
        strategy.doPromotion();
    }
}
