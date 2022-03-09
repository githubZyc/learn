package factory.factorymethod.pay;

import java.util.Scanner;

/**
 * 功能描述:
 * 通过工厂方法生产对应的支付类型
 * @Class Test
 * @Author ZYC
 * @Date 2021/4/1 9:47
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        System.out.println("请输入支付方式，获取支付流程：");
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();
        AbstractPayFactory abstractPayFactory = null;
        PayClient payClient = null;
        switch (next){
            case "ali":
                abstractPayFactory = new ALiPayFactory();
                payClient = abstractPayFactory.newPayClient();
                break;
            case "wechat":
                abstractPayFactory = new WechatPayFactory();
                payClient = abstractPayFactory.newPayClient();
                break;
            default:
                break;
        }
        payClient.preparePay();
        payClient.pay();
        payClient.refund();
    }
}
