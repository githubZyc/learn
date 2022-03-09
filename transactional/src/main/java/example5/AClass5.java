package example5;

import com.media.config.dao.VoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 功能描述:
 * 两个类AClass、BClass。AClass类有aFunction、BClass类有bFunction。
 * AClass类aFunction调用BClass类bFunction。最终会在外部调用AClass类的aFunction。
 *
 * 情况0：aFunction添加注解，bFunction不添加注解。bFunction抛异常。
 *
 * 两个函数对数据库的操作都回滚了。
 * @Class AClass
 * @Author ZYC
 * @Date 2021/3/24 10:37
 * @Version 1.0
 **/
@Service
public class AClass5 {
    @Resource
    VoteMapper voteMapper;
    @Autowired
    private BClass5 bClass5;

    @Transactional(rollbackFor = Exception.class)
    public void aFunction() {
        //delete vote
        int delete = this.voteMapper.deleteByPrimaryKey("4dm57b0i7yhlj0co53q4p2aeklhbvvwe");
        bClass5.bFunction();
    }
}
