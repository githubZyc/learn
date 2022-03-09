package prototype.citation;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:
 * 发奖状
 * @Class ProtoTypeCitation
 * @Author ZYC
 * @Date 2021/3/31 14:28
 * @Version 1.0
 **/
public class ProtoTypeCitation {
//    public static void main(String[] args) throws CloneNotSupportedException {
//        //第一个获得奖状的人
//        Citation citation = new Citation("ZYC","同学：在2016学年第一学期中表现优秀，被评为三好学生。","北京航空航天大学");
//        //第二个获得奖状的人
//        Citation citation2 = (Citation) citation.clone();
//        citation2.setName("WX");
//        System.out.println(citation2.toString());
//        //第三个获得奖状的人
//        Citation citation3 = (Citation) citation.clone();
//        citation3.setName("YJ");
//        System.out.println(citation3.toString());;
//    }

    public static class Data1{
        private int id;
        private String name;
        private int amount;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public Data1(int id, String name, int amount) {
            this.id = id;
            this.name = name;
            this.amount = amount;
        }
    }

    public static class Data2{
        private int id;
        private String name;
        private String type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Data2(int id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }

    public class OutPutData{
        private int id;
        private String name;
        private int amount;
        private String type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public OutPutData(int id, String name, int amount, String type) {
            this.id = id;
            this.name = name;
            this.amount = amount;
            this.type = type;
        }
    }

    public static void main(String[] args) {
        List<Data2> listOfData2 = new ArrayList<Data2>();

        listOfData2.add(new Data2(10501, "JOE"  , "Type1"));
        listOfData2.add(new Data2(10603, "SAL"  , "Type5"));
        listOfData2.add(new Data2(40514, "PETER", "Type4"));
        listOfData2.add(new Data2(59562, "JIM"  , "Type2"));
        listOfData2.add(new Data2(29415, "BOB"  , "Type1"));
        listOfData2.add(new Data2(61812, "JOE"  , "Type9"));
        listOfData2.add(new Data2(98432, "JOE"  , "Type7"));
        listOfData2.add(new Data2(62556, "JEFF" , "Type1"));
        listOfData2.add(new Data2(10599, "TOM"  , "Type4"));


        List<Data1> listOfData1 = new ArrayList<Data1>();
        listOfData1.add(new Data1(10501, "JOE"    ,3000000));
        listOfData1.add(new Data1(10603, "SAL"    ,6225000));
        listOfData1.add(new Data1(40514, "PETER"  ,2005000));
        listOfData1.add(new Data1(59562, "JIM"    ,3000000));
        listOfData1.add(new Data1(29415, "BOB"    ,3000000));

    }

}
