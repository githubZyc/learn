package prototype;

/**
 * 功能描述:
 *
 * @Class TestPrototype
 * @Author ZYC
 * @Date 2021/3/31 14:12
 * @Version 1.0
 **/
public class TestPrototype {
    public static void main(String[] args) {
        ChildPrototype childPrototype = new ChildPrototype();
        Object o = childPrototype.cloneParent();
    }
}
