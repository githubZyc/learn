package strategy.prize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 功能描述:
 * 工厂方法将具体实例的选择进行了封装;
 * 而客户端，也就是我们的调用方只需要调用工厂的具体方法获取到具体的事例即可，而不需要管具体的实例实现是什么。
 * @Class PrizeSenderFactory
 * @Author ZYC
 * @Date 2021/3/26 14:36
 * @Version 1.0
 **/
@Component
public class PrizeSenderFactory {
    @Autowired
    private List<PrizeSender> prizeSenders;

    /**
     * 功能描述: 生产工厂中获取对应可以出来礼物发送的实现
     * @Author ZYC
     * @Date 2021/3/26 14:38
     * @Param [request]
     * @Return com.media.sync.strategy.prize.PrizeSender
     * @Version 1.0
     **/
    public PrizeSender getPrizeSender(SendPrizeRequest request) {
        for (PrizeSender prizeSender : prizeSenders) {
            if (prizeSender.support(request)) {
                return prizeSender;
            }
        }
        throw new UnsupportedOperationException("unsupported request: " + request);
    }
}
