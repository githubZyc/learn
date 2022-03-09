package adapter.obj.motor;

/**
 * 功能描述:
 * 新能源汽车的发动机有电能发动机（Electric Motor）
 * @Class ElectricMotorAdapter
 * @Author ZYC
 * @Date 2021/4/6 11:04
 * @Version 1.0
 **/
public class ElectricMotorAdapter implements Motor{
    private ElectricMotor electricMotor;

    public ElectricMotorAdapter (ElectricMotor electricMotor) {
        this.electricMotor = electricMotor;
    }

    @Override
    public void drive() {
        this.electricMotor.electricDrive();
    }
}
