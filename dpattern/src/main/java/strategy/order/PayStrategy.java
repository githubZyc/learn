package strategy.order;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:
 * 支付策略配置
 * @Class PayStrategy
 * @Author ZYC
 * @Date 2021/3/16 15:34
 * @Version 1.0
 **/
public class PayStrategy {
    public static final String ALI_PAY = "AliPay";
    public static final String JD_PAY = "JdPay";
    public static final String WECHAT_PAY = "WechatPay";
    public static final String BANKUINION_PAY = "BankUnionPay";
    public static final String DEFAULT_PAY = "AliPay";

    private static Map<String,Payment> strategyMap = new HashMap<String, Payment>();
    static {
        strategyMap.put(ALI_PAY,new AliPay());
        strategyMap.put(JD_PAY,new JDPay());
        strategyMap.put(WECHAT_PAY,new WechatPay());
        strategyMap.put(BANKUINION_PAY,new BankUnionPay());
    }

    public static Payment get(String payKey){
        if(!strategyMap.containsKey(payKey)){
            return strategyMap.get(DEFAULT_PAY);
        }
        return strategyMap.get(payKey);
    }
}
