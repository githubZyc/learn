package adapter.clas;

/**
 * 功能描述:
 *
 * @Class ClassAdapterTest
 * @Author ZYC
 * @Date 2021/4/2 16:02
 * @Version 1.0
 **/
public class ClassAdapterTest {
    public static void main(String[] args) {
        Target target = new ClassAdapter();
        target.request();
    }
}
