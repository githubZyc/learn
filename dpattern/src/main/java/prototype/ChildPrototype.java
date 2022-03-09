package prototype;

/**
 * 功能描述:
 *
 * @Class ChildPrototype
 * @Author ZYC
 * @Date 2021/3/31 14:11
 * @Version 1.0
 **/
public class ChildPrototype extends ParentPrototype {
    @Override
    public Object cloneParent() {
        try {
            return clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
