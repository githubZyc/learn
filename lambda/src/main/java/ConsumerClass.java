import java.util.function.Consumer;

/**
 * 功能描述:
 * 输入一个参数进行消费，无返回值
 * @Class ConsumerClass
 * @Author ZYC
 * @Date 2021/4/2 9:51
 * @Version 1.0
 **/
public class ConsumerClass {
    public static void main(String[] args) {

        //参数t : consumer:
        Consumer consumer1 = (t) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(t).append("hello");
            System.out.println(stringBuilder.toString());
        };

        //参数t : consumer:
        Consumer consumer2 = (t) ->{
            System.out.println(t + "world");
        };

        //分开消费传入的参数， consumer1执行结果：consumer: hello
        // consumer2执行结果：consumer: world
        consumer1.andThen(consumer2).accept("consumer: ");

    }
}
