package 最大加法;

public class MaxAdd {

    public static String add(String s1,String s2){
        int n = s1.length()-1;
        int m = s2.length()-1;
        int curry = 0;
        StringBuilder builder = new StringBuilder("");
        while (n>=0 || m>=0){
            int tmp = (n<0?0:s1.charAt(n--)-'0') + (m<0 ? 0 : s2.charAt(m--)-'0') + curry;
            if(tmp<10){
                curry = 0;
            }else{
                curry = 1;
            }
            builder.append(tmp%10);
        }
        if(curry > 0){
            builder.append("1");
        }
        return builder.reverse().toString();
    }



    public static String addBigNumber(String s1,String s2){
        int zero = s1.length() >= s2.length() ? s1.length() - s2.length() : s2.length() - s1.length();
        if(s1.length() > s2.length()){
            s2 = addZero(zero,s2);
        }else{
            s1 = addZero(zero,s1);
        }
        // 12 01
        StringBuilder re = new StringBuilder("");
        int curry = 0;
        for (int i = (s1.length() - 1); i >= 0; i--) {
            int tmp =  (s1.charAt(i) - '0' + s2.charAt(i) - '0') + curry;
            if(tmp>=10){
                curry = 1;
            }else{
                curry = 0;
            }
            re.append(tmp%10);
        }
        if(curry>0){
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
        System.out.println(add("114514","20"));
    }
}
