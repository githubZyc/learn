import java.util.*;

/**
 * 功能描述:
 *
 * @Class CollectionsClass
 * @Author ZYC
 * @Date 2021/8/23 9:19
 * @Version 1.0
 **/
public class CollectionsClass {
    public static Integer testBinarySearch(List<String> data,String key){
        //二分法查找，以5000为限制。大于5000  改成遍历查找
        int index = Collections.binarySearch(data, key);
        return index;
    }

    public static Map<Integer,String> testCheckMap(Map<Integer,String> hm){
        Map<Integer, String> integerStringMap = Collections.checkedMap(hm, Integer.class, String.class);
        System.out.println(integerStringMap);
        //"filter: 过滤  也就是留下的意思！ 过滤 isnotnull 即留下不为null的数据";
        return integerStringMap;
    }

    public static void testFillList(List<String> origin,String fillKey){
        //容器内的所有元素被全部替换成目标元素
        Collections.fill(origin,fillKey);
    }

    public static int testFrequency(List<String> origin,String frequencyStr){
        //容器内于目标相同的元素个数
        int t = Collections.frequency(origin, frequencyStr);
        return t;
    }

    public static int testIndexOfSubList(List<?> source,List<?> target){
        //指定源列表中指定目标列表的第一次出现的起始位置，如果没有，则返回-1
        int i = Collections.indexOfSubList(source, target);
        return i;
    }

    public static int testLastIndexOfSubList(List<String> origin, List<String> target){
        int i = Collections.lastIndexOfSubList(origin, target);
        return 0;

    }


    public static void main(String[] args) {
        List<String> origin = new ArrayList<>(10);
        origin.add("1");
        origin.add("c");
        origin.add("d");
        origin.add("1");
        origin.add("1");
        origin.add("1");
        int i = testFrequency(origin, "1");
        System.out.println(i);

//        List<String> origin = new ArrayList<>(10);
//        origin.add("1");
//        origin.add("c");
//        origin.add("d");
//        testFillList(origin,"hello world");
//        System.out.println(origin);

//        Map<Integer,String> hm = new HashMap<>(5);
//        hm.put(20,"C");
//        hm.put(10,"C++");
//        hm.put(30,"JAVA");
//        hm.put(40,"DOTNET");
//        hm.put(50,"PHP");
//        Map<Integer, String> integerStringMap = testCheckMap(hm);

//        Integer integer = testBinarySearch(Arrays.asList("qazwsxedcrfvtgbyhnujmik".split("")), "q");
//        System.out.println(integer);
    }

}
