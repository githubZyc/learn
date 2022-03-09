package adapter.obj;

/**
 * 功能描述:
 *
 * @Class ObjectAdapter
 * @Author ZYC
 * @Date 2021/4/2 16:05
 * @Version 1.0
 **/
public class ObjectAdapterTest {
    public static void main(String[] args) {
        Adaptee adaptee = new Adaptee();
        Target target = new ObjectAdapter(adaptee);
        target.request();
    }
}
