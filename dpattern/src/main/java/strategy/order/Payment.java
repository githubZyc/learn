package strategy.order;

import com.media.common.util.MsgUtil;

public interface Payment {
    String getName();

    default String pay(String uid, double amount){
        //查询余额是否足够
        if(queryBalance(uid) < amount){
            return MsgUtil.fail("支付失败,余额不足");
        }
        return MsgUtil.success("支付成功,支付金额："+amount);
    }

    double queryBalance(String uid);
}
