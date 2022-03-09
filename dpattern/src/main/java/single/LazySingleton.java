package single;

/**
 * 功能描述:
 * 懒汉式单例:调用方法时生成实例对象
 * @Class LazySingleton
 * @Author ZYC
 * @Date 2021/3/31 10:41
 * @Version 1.0
 **/
public class LazySingleton {
    //保证多个线程间访问时线程间可见
    private static volatile LazySingleton instance = null;

    //私有的构造函数
    private LazySingleton(){}

    //获取实例方法
    public static synchronized LazySingleton getInstance(){
        if(instance == null){
            instance = new LazySingleton();
        }
        return instance;
    }
}
