package adapter.obj;

/**
 * 功能描述:
 * 它是一个转换器，通过继承或引用适配者的对象，
 * 把适配者接口转换成目标接口，让客户按目标接口的格式访问适配者。
 * @Class ObjectAdapter
 * @Author ZYC
 * @Date 2021/4/2 16:05
 * @Version 1.0
 **/
public class ObjectAdapter implements Target {
    private Adaptee adaptee;

    ObjectAdapter(Adaptee adaptee){
        this.adaptee = adaptee;
    }
    @Override
    public void request() {
        this.adaptee.specificRequest();
    }
}
