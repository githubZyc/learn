package prototype.shape;

/**
 * 功能描述:
 * 具体原型
 * @Class Shape
 * @Author ZYC
 * @Date 2021/3/31 14:37
 * @Version 1.0
 **/
public abstract class Shape implements Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected abstract Double countArea();
}
