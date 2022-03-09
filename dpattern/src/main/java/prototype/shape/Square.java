package prototype.shape;

import java.util.Scanner;

/**
 * 功能描述:
 * 长方形
 * @Class Square
 * @Author ZYC
 * @Date 2021/3/31 14:44
 * @Version 1.0
 **/
public class Square extends Shape {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Square b = null;
        try {
            b = (Square) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("拷贝正方形失败!");
        }
        return b;
    }

    @Override
    protected Double countArea() {
        Double a = 0.0;
        System.out.print("这是一个正方形，请输入它的边长：");
        Scanner input = new Scanner(System.in);
        a = input.nextDouble();
        double i = a * a;
        System.out.println("该正方形的面积=" + i + "\n");
        return i;
    }
}
