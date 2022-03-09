package adapter.obj.motor;

/**
 * 功能描述:
 * 光能适配器
 * 适配器模式主要角色：
 * 目标接口：target interface
 * 适配者： adaptee 它是被访问和适配的现存组件库中的组件接口。
 * 适配器：adapter 通过继承或引用适配者的对象，
 *                  把适配者接口转换成目标接口，让客户按目标接口的格式访问适配者。
 * @Class ElectricAdapter
 * @Author ZYC
 * @Date 2021/4/6 11:09
 * @Version 1.0
 **/
public class OpticalMotorAdapter implements Motor{

    OpticalMotor opticalMotor;

    @Override
    public void drive() {

    }
}
