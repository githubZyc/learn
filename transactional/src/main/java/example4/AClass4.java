package example4;

import com.media.config.dao.VoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 功能描述:
 * 同一个类AClass中，有两个函数aFunction、aInnerFunction。
 * aFunction调用aInnerFunction。而且aFunction函数会被外部调用。
 * aFunction添加了@Transactional注解，aInnerFunction函数没有添加。
 * aInnerFunction抛异常，不过在aFunction里面把异常抓出来了。
 *
 * 结果：
 * 两个函数里面的数据库操作都成功。
 * 事务回滚的动作发生在当有@Transactional注解函数有对应异常抛出时才会回滚。
 * (当然了要看你添加的@Transactional注解有没有效)。
 * @Class AClass
 * @Author ZYC
 * @Date 2021/3/23 9:32
 * @Version 1.0
 **/
@Service
public class AClass4 {
    @Resource
    VoteMapper voteMapper;

    @Transactional(rollbackFor = Exception.class)
    public void aFunction() {
        //delete vote
        int delete = this.voteMapper.deleteByPrimaryKey("1ft9pwmxcpcvfc1wytpsbebrkg7qjkog");
        try {
            aInnerFunction(); // 调用内部没有添加@Transactional注解的函数
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aInnerFunction() {
        int delete = this.voteMapper.deleteByPrimaryKey("3gyhhxhba8j5d6qagok2cx7zkhok6488");
        throw new RuntimeException("函数执行有异常!");
    }
}
