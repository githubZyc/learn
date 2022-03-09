import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 功能描述:
 * 输入一个T 返回一个R
 * @Class FunctionClass
 * @Author ZYC
 * @Date 2021/4/1 18:40
 * @Version 1.0
 **/
public class PredicateClass {
    public static void main(String[] args)  {
        List<Integer> integers = Arrays.asList(1, 3, 6, 8, 9, 200, 109);
        //定义谓词
        // -- 偶数
        Predicate<Integer> predicate =  (num) -> num % 2 == 0 ;
        // --大于100
        Predicate<Integer> predicate2 =  (num) -> num>100 ;
        // --<=200
        Predicate<Integer> predicate3 =  (num) -> num<=200 ;

        List<Predicate<Integer>>allP = new ArrayList<>(3);
        allP.add(predicate);
        allP.add(predicate2);
        allP.add(predicate3);

        List<Integer> collect = integers.stream().filter(allP.stream().reduce(x ->true,Predicate::and)).collect(Collectors.toList());
        System.out.println(collect);


//        List<String> lst = new ArrayList(5);
//        lst.add("java");
//        lst.add("php");
//        lst.add("python");
//        lst.add("hadoop");
//        String str = predicateFilter(lst,(s) -> s.startsWith("j") );
    }

    static String predicateFilter(List<String> lst, Predicate<String> predicate){
        lst.forEach((str) ->{
            if(predicate.test(str)){
                System.out.println(str);
            }
        });
        return "";
    }
}
