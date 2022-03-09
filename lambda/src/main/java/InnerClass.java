import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:
 *
 * @Class InnerClass
 * @Author ZYC
 * @Date 2021/7/5 17:01
 * @Version 1.0
 **/
public class InnerClass {
    //内部接口
    interface Action{
        void run();
    }

    public static class ShareClosure{
        //list 中存储泛型接口,可以多个实现 构造对象
        List<Action> list = new ArrayList<>();

        public void input(){
            for (int i = 0; i < 10; i++) {
                final int finalI = i;
                list.add(new Action() {
                    @Override
                    public void run() {
                        System.out.println(finalI);
                    }
                });
            }
        }

        public void output(){
            for(Action action : list){
                action.run();
            }
        }

    }

    public static void main(String[] args) {
        ShareClosure shareClosure = new ShareClosure();
        shareClosure.input();
//        shareClosure.output();
    }

}
