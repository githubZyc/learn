package 最大加法;

public class MaxAdd {

    public static String addBigNumber(String s1,String s2){
        int zero = s1.length() >= s2.length() ? s1.length() - s2.length() : s2.length() - s1.length();
        if(s1.length() > s2.length()){
            s2 = addZero(zero,s2);
        }else{
            s1  = addZero(zero,s1);
        }
        // 12 01
        StringBuilder re = new StringBuilder("");
        int shi = 0;
        for (int i = (s1.length() - 1); i >= 0; i--) {
            int tmp =  (s1.charAt(i) - '0' + s2.charAt(i) - '0') + shi;
            if(tmp>=10){
                shi = 1;
            }else{
                shi = 0;
            }
            re.append(tmp%10);
        }
        if(shi>0){
            re.append(1);
        }
        return re.reverse().toString();
    }
    public static String addZero(int zero, String num) {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < zero; i++) {
            s.append("0");
        }
        s.append(num);
        return s.toString();
    }

    public static void main(String[] args) {
        System.out.println(addBigNumber("114514","20"));
    }
}
