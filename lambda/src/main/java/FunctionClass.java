import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * 功能描述:
 * 输入一个T 返回一个R
 * @Class FunctionClass
 * @Author ZYC
 * @Date 2021/4/1 18:40
 * @Version 1.0
 **/
public class FunctionClass {

    /**
     * 功能描述: 传入一个值，计算后返回
     * @Author ZYC
     * @Date 2021/4/1 18:51
     * @Param [valueToBeOperated, function]
     * @Return int
     * @Version 1.0
     **/
    static int modifyTheValue(int valueToBeOperated, Function<Integer, Integer> function) {
        return function.apply(valueToBeOperated);
    }

    /**
     * 功能描述:  value作为function1的参数，返回一个结果，该结果作为function2的参数，返回一个最终结果
     * @Author ZYC
     * @Date 2021/4/1 18:50
     * @Param [valueToBeOperated, function1, function2]
     * @Return int
     * @Version 1.0
     **/
    static int modifyTheValue2(int valueToBeOperated, Function<Integer, Integer> functionBefore,Function<Integer, Integer> functionAfter) {
        /**
         参数 valueToBeOperated 经过function 1 的处理后得到结果，将结果传入到function 2,之后做运算
         **/
        return functionBefore.andThen(functionAfter).apply(valueToBeOperated);
    }

    static int modifyBeforeTheValue(int valueBefore,Function<Integer,Integer> functionBefore,Function<Integer,Integer> functionAfter){
        /**
         参数 valueBefore 经过function 2 的处理后得到结果，将结果传入到function 1,之后做运算
         **/
        return functionBefore.compose(functionAfter).apply(valueBefore);
    }

    public static void main(String[] args) {
////        Function<String,String> getStr = (a) ->  a+ "World" ;
////        String tst = getStr.apply("Hello ");
////        System.out.println(tst);
//        int i = modifyBeforeTheValue(10, a ->{
//            System.out.println("后到1:" + a);
//            return  a + 1;
//        }, b-> {
//            System.out.println("先到2:" + b);
//            return  b -2;
//        });
//        System.out.println(i);

        BiFunction<Integer,Integer,Integer> biFunction = (x,y) -> x+y;
        BinaryOperator<Integer> binaryOperator = (x,y) -> x+y;

        Integer apply1 = biFunction.apply(1, 3);
        Integer apply2 = binaryOperator.apply(1, 3);

        System.out.println(apply1);
        System.out.println(apply2);

    }
}
