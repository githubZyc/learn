package strategy.prize;

import org.springframework.stereotype.Component;

/**
 * 功能描述:
 * 现金奖励发放
 * @Class CashSender
 * @Author ZYC
 * @Date 2021/3/26 14:26
 * @Version 1.0
 **/
@Component
public class CashSender implements PrizeSender{
    @Override
    public boolean support(SendPrizeRequest sendPrizeRequest) {
        return SendPrizeRequest.PrizeTypeEnum.CASH == sendPrizeRequest.getPrizeType();
    }

    @Override
    public void sendPrize(SendPrizeRequest request) {
        System.out.println("发放现金");
    }
}
