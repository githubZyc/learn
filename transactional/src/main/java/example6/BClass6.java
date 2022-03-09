package example6;

import com.media.config.dao.VoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 功能描述:
 * 两个类AClass、BClass。AClass类有aFunction、BClass类有bFunction。
 * AClass类aFunction调用BClass类bFunction。最终会在外部调用AClass类的aFunction。
 *
 * 情况0：aFunction添加注解，bFunction不添加注解。bFunction抛异常。
 * 两个函数对数据库的操作都回滚了。
 * @Class BClass
 * @Author ZYC
 * @Date 2021/3/24 10:38
 * @Version 1.0
 **/
@Service
public class BClass6 {
    @Resource
    VoteMapper voteMapper;

    @Transactional(rollbackFor = Exception.class)
    public void bFunction() {
        int delete = this.voteMapper.deleteByPrimaryKey("6bkkt1h40xfz633bbuzgidnkkejm25qr");
        throw new RuntimeException("函数执行有异常!");
    }
}
