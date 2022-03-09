package strategy.prize;

import org.springframework.stereotype.Component;

/**
 * 功能描述:
 * 虚拟币奖励发放
 * @Class VirtualCurrencySender
 * @Author ZYC
 * @Date 2021/3/26 14:26
 * @Version 1.0
 **/
@Component
public class VirtualCurrencySender implements PrizeSender{
    @Override
    public boolean support(SendPrizeRequest sendPrizeRequest) {
        return SendPrizeRequest.PrizeTypeEnum.VIRTUAL_CURRENCY == sendPrizeRequest.getPrizeType();
    }

    @Override
    public void sendPrize(SendPrizeRequest request) {
        System.out.println("发放虚拟币");
    }
}
