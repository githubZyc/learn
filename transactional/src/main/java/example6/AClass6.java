package example6;

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
 * 情况1：aFunction、bFunction两个函数都添加注解，bFunction抛异常。aFunction抓出异常。
 *
 * 结果：两个函数数据库操作都没成功。而且还抛异常了。
 * org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only。
 * 看打印出来的解释也很好理解把。咱们也可以这么理解，两个函数用的是同一个事务。
 * bFunction函数抛了异常，调了事务的rollback函数。
 * 事务被标记了只能rollback了。程序继续执行，aFunction函数里面把异常给抓出来了，这个时候aFunction函数没有抛出异常，既然你没有异常那事务就需要提交，会调事务的commit函数。而之前已经标记了事务只能rollback-only(以为是同一个事务)。直接就抛异常了，不让调了。
 *
 * @Class AClass
 * @Author ZYC
 * @Date 2021/3/24 10:37
 * @Version 1.0
 **/
@Service
public class AClass6 {
    @Resource
    VoteMapper voteMapper;
    @Autowired
    private BClass6 bClass6;

    @Transactional(rollbackFor = Exception.class)
    public void aFunction() {
        //delete vote
        int delete = this.voteMapper.deleteByPrimaryKey("5i4bjfz39jaim9ry6ospx3sxk7bodejh");
        bClass6.bFunction();
    }
}
