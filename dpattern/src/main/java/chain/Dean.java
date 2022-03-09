package chain;

/**
 * 功能描述:
 *
 * @Class Dean
 * @Author ZYC
 * @Date 2021/4/22 17:24
 * @Version 1.0
 **/
public class Dean extends Leader {
    @Override
    void handleRequest(int LeaveDays) {
        System.out.println("院长处理请假");
    }
}
