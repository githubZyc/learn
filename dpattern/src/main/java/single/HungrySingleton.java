package single;

/**
 * 功能描述:
 * 饿汉式：类加载时生成一个对象
 * @Class LazySingleton
 * @Author ZYC
 * @Date 2021/3/31 10:41
 * @Version 1.0
 **/
public class HungrySingleton {
    private static final HungrySingleton hungrySingleton = new HungrySingleton();

    public HungrySingleton getHungrySingleton(){
        return hungrySingleton;
    }
}
