package adapter.obj.motor;

/**
 * 功能描述:
 * 客户端希望用统一的发动机驱动方法 drive() 访问这两种发动机，
 * 所以必须定义一个统一的目标接口 Motor，
 * 然后再定义电能适配器（Electric Adapter）和光能适配器（Optical Adapter）
 * 去适配这两种发动机。
 * @Class Motor
 * @Author ZYC
 * @Date 2021/4/6 10:55
 * @Version 1.0
 **/
public interface Motor {
    void drive();
}
