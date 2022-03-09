package factory.factorymethod;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/4/1 9:33
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
//        //调用工厂
//        AbstractFactory abstractFactory = new ConcreteFactory1();
//        //生产对应产品
//        Product product = abstractFactory.newProduct();
//        //生成产品
//        product.show();
        for (int i =0;i<5;i++){
           try{
               if (i==2){
                   throw new RuntimeException();
               }
               System.out.println(i);
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }
}
