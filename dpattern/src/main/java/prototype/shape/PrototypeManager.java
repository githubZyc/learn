package prototype.shape;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:
 * 图形管理器
 * @Class PrototypeManager
 * @Author ZYC
 * @Date 2021/3/31 14:45
 * @Version 1.0
 **/
public class PrototypeManager {
    Map<String,Shape> map = new HashMap<>();

    public Shape addShape(String key,Shape shape){
        return this.map.put(key,shape);
    }

    public Shape getShape(String key){
        return this.map.get(key);
    }


}
