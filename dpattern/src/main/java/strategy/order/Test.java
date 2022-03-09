package strategy.order;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/3/16 15:33
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        Order order = new Order("1","20200618012222",1.5);
        String pay = order.pay(PayStrategy.BANKUINION_PAY);
        System.out.println(pay);
    }
}
