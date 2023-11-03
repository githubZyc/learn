package template.demo1;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description 指定算法骨架
 * @date 2023/11/3 14:21
 */
public abstract class SoybeanMilk {
    /**
     * @description 指定制作豆浆步骤
     * @author  zhengyanchuang
     * @date    2023/11/3 14:23
     * @param
     * @return  void
     */
    final void milk(){
        System.out.println("-----开始制作豆浆-----");
        //选材
        select();
        //使用红黑什么豆
        addCondiment();
        //一起浸泡
        soak();
        //制作
        beat();
        System.out.println("-----豆浆制作完成-----");
    }

    /**
     * @description 制作豆浆
     * @author  zhengyanchuang
     * @date    2023/11/3 14:27
     * @param
     * @return  void
     */
    private void beat() {
        System.out.println("-----豆浆制作中-----");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description 选材一起浸泡
     * @author  zhengyanchuang
     * @date    2023/11/3 14:26
     * @param
     * @return  void
     */
    private void soak() {
        System.out.println("-----将选好的豆子和其他配料一起浸泡-----");
    }

    /**
     * @description 添加什么豆子自己说算
     * @author  zhengyanchuang
     * @date    2023/11/3 14:26
     * @param
     * @return  void
     */
    protected abstract void addCondiment();

    /**
     * @description 选材
     * @author  zhengyanchuang
     * @date    2023/11/3 14:26
     * @param
     * @return  void
     */
    private void select() {
        System.out.println("-----选材一定要新鲜豆子-----");
    }
}
