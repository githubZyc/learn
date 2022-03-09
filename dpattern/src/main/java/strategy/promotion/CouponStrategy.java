package strategy.promotion;

/**
 * 功能描述:
 * 优惠券促销
 * @Class CouponStrategy
 * @Author ZYC
 * @Date 2021/3/16 15:00
 * @Version 1.0
 **/
public class CouponStrategy implements IPromotionStrategy {
    @Override
    public void doPromotion() {
        System.out.println("使用优惠券抵扣");
    }
}
