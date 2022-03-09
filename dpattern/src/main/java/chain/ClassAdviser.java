package chain;

/**
 * 功能描述:
 * 具体执行类
 * @Class ClassAdviser
 * @Author ZYC
 * @Date 2021/4/22 17:22
 * @Version 1.0
 **/
public class ClassAdviser extends Leader {
    private Integer LEAVE_DAYS = 2;

    @Override
    void handleRequest(int LeaveDays) {
        System.out.println("班主任批准假条");
        if(LeaveDays > LEAVE_DAYS){
            Leader nextLeader = this.getNextLeader();
            if(nextLeader !=null){
                nextLeader.handleRequest(LeaveDays);
            }
        }else{
            System.out.println("班主任批准" + LeaveDays +"天的假期！");
        }
    }
}
