package adapter.clas;

/**
 * 功能描述:
 *
 * @Class ClassAdapter
 * @Author ZYC
 * @Date 2021/4/2 16:00
 * @Version 1.0
 **/
public class ClassAdapter extends Adaptee implements Target {
    @Override
    public void request() {
        this.specificRequest();
    }
}
