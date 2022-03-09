import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/4/29 15:23
 * @Version 1.0
 **/
public class Test {
    public static void main1(String[] args) {
//        Stream.of("hello", "world").map(s -> s.split("")).forEach(System.out::println);
//        Stream.of("hello", "world").flatMap(s -> Stream.of(s.split(""))).forEach(System.out::println);

        List<List<String>> flatList = new ArrayList<>(2);
        List<String> lst1 = new ArrayList<>(3);
        lst1.add("hello");
        lst1.add("world");
        flatList.add(lst1);


        flatList.stream().map(strings -> { return strings;}).forEach(System.out::println);
        flatList.stream().flatMap(strings -> { return Stream.of(strings);}).forEach(System.out::println);

    }
    static class T{
        String key;
        Long number;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Long getNumber() {
            return number;
        }

        public void setNumber(Long number) {
            this.number = number;
        }
    }

    public static void main(String[] args) {

        List<T> t1 = new ArrayList<>(10);
        for (Long i =0L;i<10;i++){
            T t = new T();
            t.setKey("1"+i);
            t.setNumber(i);
            t1.add(t);
        }


        List<T> t2 = new ArrayList<>(10);
        for (Long i =0L;i<10;i++){
            T t = new T();
            t.setKey("1"+i);
            t.setNumber(i);
            t2.add(t);
        }

        Map<String, Long> collect = t1.stream().collect(Collectors.toMap(T::getKey, T::getNumber));
        Map<String, Long> collect2 = t2.stream().collect(Collectors.toMap(T::getKey, T::getNumber));
    }
}
