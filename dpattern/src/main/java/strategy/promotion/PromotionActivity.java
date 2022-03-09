package strategy.promotion;

/**
 * 功能描述:
 *
 * @Class PromotionActivity
 * @Author ZYC
 * @Date 2021/3/16 15:02
 * @Version 1.0
 **/
public class PromotionActivity {
    private IPromotionStrategy strategy;

    PromotionActivity (IPromotionStrategy iPromotionStrategy){
        this.strategy = iPromotionStrategy;
    }

    public void execute(){
        this.strategy.doPromotion();
    }
}
