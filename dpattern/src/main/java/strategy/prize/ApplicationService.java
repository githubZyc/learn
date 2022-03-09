package strategy.prize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述:
 * 客户端调用
 * @Class ApplicationService
 * @Author ZYC
 * @Date 2021/3/26 14:39
 * @Version 1.0
 **/
@Service
public class ApplicationService {
    @Autowired
    PrizeSenderFactory prizeSenderFactory;

    public void mockedClient(){
        SendPrizeRequest sendPrizeRequest = new SendPrizeRequest();
        // 这里的request一般是根据数据库或外部调用来生成的
        sendPrizeRequest.setPrizeType(SendPrizeRequest.PrizeTypeEnum.POINT);
        sendPrizeRequest.setSize(10);
        sendPrizeRequest.setUserId("110");
        sendPrizeRequest.setPrizeId("1");
        //从策略工厂中获取可执行的接口
        PrizeSender prizeSender = prizeSenderFactory.getPrizeSender(sendPrizeRequest);
        prizeSender.sendPrize(sendPrizeRequest);
    }
}
