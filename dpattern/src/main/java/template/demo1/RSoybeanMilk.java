package template.demo1;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description 制作红豆浆
 * @date 2023/11/3 14:32
 */
public class RSoybeanMilk extends SoybeanMilk{
    @Override
    protected void addCondiment() {
        System.out.println("红豆子选品完成开始进行下一步");
    }
}
