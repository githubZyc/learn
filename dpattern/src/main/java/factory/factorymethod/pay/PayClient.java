package factory.factorymethod.pay;

/**
 * 功能描述: 对应支付类型支付通用方法
 * @Author ZYC
 * @Date 2021/4/1 9:39
 * @Param
 * @Return
 * @Version 1.0
 **/
public interface PayClient {
    /**
     * 预支付接口
     * @return
     */
    String preparePay();

    /**
     * 支付
     * @return
     */
    String pay();

    /**
     * 退款
     * @return
     */
    String refund();
}
