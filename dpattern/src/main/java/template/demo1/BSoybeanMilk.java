package template.demo1;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description 制作黑豆浆
 * @date 2023/11/3 14:32
 */
public class BSoybeanMilk extends SoybeanMilk{
    /**
     * @description 选用黑色的豆子制作黑豆浆
     * @author  zhengyanchuang
     * @date    2023/11/3 14:33
     * @param
     * @return  void
     */
    @Override
    protected void addCondiment() {
        System.out.println("黑豆子选品完成开始进行下一步");
    }
}
