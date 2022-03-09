package strategy.prize;

import org.springframework.stereotype.Component;

/**
 * 功能描述:
 * 积分奖励发放
 * @Class PointSender
 * @Author ZYC
 * @Date 2021/3/26 14:25
 * @Version 1.0
 **/
@Component
public class PointSender implements PrizeSender {
    @Override
    public boolean support(SendPrizeRequest sendPrizeRequest) {
        return sendPrizeRequest.getPrizeType() == SendPrizeRequest.PrizeTypeEnum.POINT;
    }

    @Override
    public void sendPrize(SendPrizeRequest request) {
        System.out.println("发放积分");
    }
}
