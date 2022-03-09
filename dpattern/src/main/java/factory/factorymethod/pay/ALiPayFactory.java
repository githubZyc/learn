package factory.factorymethod.pay;

/**
 * 功能描述:
 * 阿里支付工厂
 * @Class ALiPayFactory
 * @Author ZYC
 * @Date 2021/4/1 9:42
 * @Version 1.0
 **/
public class ALiPayFactory implements AbstractPayFactory {
    @Override
    public PayClient newPayClient() {
        /**
         * 生成 ali 支付客户端
         *
         * init
         *
         * create
         */
        return new AliPayClient();
    }
}
