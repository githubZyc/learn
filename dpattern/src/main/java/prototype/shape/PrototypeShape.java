package prototype.shape;

/**
 * 功能描述:
 *
 * @Class PrototypeShape
 * @Author ZYC
 * @Date 2021/3/31 14:48
 * @Version 1.0
 **/
public class PrototypeShape {
    public static void main(String[] args) {
        PrototypeManager prototypeManager = new PrototypeManager();
        prototypeManager.addShape("circle",new Circle());
        prototypeManager.addShape("square",new Square());

        Shape circle = prototypeManager.getShape("circle");
        circle.countArea();
        Shape square = prototypeManager.getShape("square");
        square.countArea();

    }
}
