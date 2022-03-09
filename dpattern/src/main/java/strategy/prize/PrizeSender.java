package strategy.prize;

/**
 * 功能描述: 礼物发放
 * @Author ZYC
 * @Date 14:19
 * @Param
 * @Return
 * @Version 1.0
 **/
public interface PrizeSender {
    /**
     * 功能描述: 用于判断当前实例是否支持当前奖励的发放
     * @Author ZYC
     * @Date 2021/3/26 14:20
     * @Param 
     * @Return 
     * @Version 1.0
     **/
    boolean support(SendPrizeRequest sendPrizeRequest);

    /**
     * 功能描述: 发放奖励
     * @Author ZYC
     * @Date 2021/3/26 14:20
     * @Param
     * @Return
     * @Version 1.0
     **/
    void sendPrize(SendPrizeRequest request);
}
