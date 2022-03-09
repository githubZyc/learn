package chain;

/**
 * 功能描述:
 * 抽象处理者（Handler）角色：定义一个处理请求的接口，包含抽象处理方法和一个后继连接
 * @Class Leader
 * @Author ZYC
 * @Date 2021/4/22 17:19
 * @Version 1.0
 **/
public abstract class Leader {
    private Leader leader;

    public Leader getNextLeader(){
        return this.leader;
    }

    public void setNextLeader(Leader nextLeader){
        this.leader = nextLeader;
    }

    //具体链上执行方法
    abstract void handleRequest(int LeaveDays);
}
