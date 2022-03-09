package prototype.shape;

import java.util.Scanner;

/**
 * 功能描述:
 * 圆形
 * @Class Circle
 * @Author ZYC
 * @Date 2021/3/31 14:43
 * @Version 1.0
 **/
public class Circle extends Shape {

    protected Object clone() throws CloneNotSupportedException {
        Circle w = null;
        try {
            w = (Circle) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("拷贝圆失败!");
        }
        return w;
    }

    @Override
    protected Double countArea() {
        int r = 0;
        System.out.print("这是一个圆，请输入圆的半径：");
        Scanner input = new Scanner(System.in);
        r = input.nextInt();
        double v = 3.1415 * r * r;
        System.out.println("该圆的面积=" + v + "\n");
        return v;
    }
}
