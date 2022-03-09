package strategy.promotion;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:
 * 上下文对象创建工厂
 * @Class PromotionStrategyFactory
 * @Author ZYC
 * @Date 2021/3/16 15:03
 * @Version 1.0
 **/
public class PromotionStrategyFactory {
    //策略工厂器
    private static Map<String,IPromotionStrategy> promotionStrategyMap = new HashMap<String,IPromotionStrategy>();
    //默认购买
    private static final IPromotionStrategy EMPTY = new EmptyStrategy();

    //容器默认存储策略
    static{
        promotionStrategyMap.put(PromotionKey.COUPON,new CouponStrategy());
        promotionStrategyMap.put(PromotionKey.CASH_ROLLBACK,new CashRollbackStrategy());
        promotionStrategyMap.put(PromotionKey.GROUP_PURCHASE,new GroupPurchaseStrategy());
    }

    private PromotionStrategyFactory(){}

    //获取对应策略
    static IPromotionStrategy getPromotionStrategy(String promotionKey){
        IPromotionStrategy strategy = promotionStrategyMap.get(promotionKey);
        return strategy == null ? EMPTY : strategy;
    }

    //定义内部接口，定义key
    private interface PromotionKey{
        String COUPON = "COUPON";
        String CASH_ROLLBACK = "CASH_ROLLBACK";
        String GROUP_PURCHASE = "GROUP_PURCHASE";
    }
}
