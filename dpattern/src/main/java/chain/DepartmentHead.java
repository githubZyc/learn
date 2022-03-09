package chain;

/**
 * 功能描述:
 * 具体处理者（Concrete Handler）角色：
 * 否则将该请求转给它的后继者。
 * 实现抽象处理者的处理方法，判断能否处理本次请求，如果可以处理请求则处理，
 * @Class DepartmentHead
 * @Author ZYC
 * @Date 2021/4/22 17:22
 * @Version 1.0
 **/
public class DepartmentHead extends Leader {
    private Integer LEAVE_DAYS = 5;

    @Override
    void handleRequest(int LeaveDays) {
        System.out.println("系主任处理请假");
        if(LeaveDays > LEAVE_DAYS){
            Leader nextLeader = this.getNextLeader();
            if(nextLeader !=null){
                nextLeader.handleRequest(LeaveDays);
            }else{
                System.out.println("无人能处理您的"+LEAVE_DAYS+"假期");
            }
        }else{
            System.out.println("系主任批准" + LeaveDays +"天的假期！");
        }
    }
}
