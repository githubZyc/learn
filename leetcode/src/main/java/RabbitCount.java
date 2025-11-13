/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/11/14 11:59
 */
public class RabbitCount {
    public static void main(String[] args) {
        int month = 50;
        // 初始化前两个月的兔子数量
        long fistMonthRabbitCount = 1, nextMonthRabbitCount = 1;

        // 迭代计算第n个月的兔子总数
        for (int i = 3; i <= month; i++) {
            long temp = fistMonthRabbitCount + nextMonthRabbitCount;
            fistMonthRabbitCount = nextMonthRabbitCount;
            nextMonthRabbitCount = temp;
        }

        System.out.println("第" + month + "个月的兔子总数是: " + nextMonthRabbitCount);
    }
}
