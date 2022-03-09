/**
 * 功能描述:
 *
 * @Class InterfaceFunction
 * @Author ZYC
 * @Date 2021/7/5 17:21
 * @Version 1.0
 **/
public class InterfaceFunction {

    @FunctionalInterface
    interface IConsumer{
        void say();
    }

    @FunctionalInterface
    interface IFunction{
        String getStr(String s, Integer length);
    }


    private void runConsumer(IConsumer iConsumer){
        iConsumer.say();
    }

    private String runFunction(IFunction iFunction){
        return iFunction.getStr("a",10);
    }

    public static void main(String[] args) {
        InterfaceFunction interfaceFunction = new InterfaceFunction();
        interfaceFunction.runConsumer(()-> System.out.println("hello interface function"));

        String newString = interfaceFunction.runFunction(((s, length) -> {
            StringBuilder sbf = new StringBuilder();
            for (int i =0;i<length;i++){
                sbf.append(s);
            }
            return sbf.toString();
        }));

        System.out.println(newString);
    }
}
