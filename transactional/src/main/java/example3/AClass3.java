package example3;

import com.media.config.dao.VoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 功能描述:
 * 同一个类AClass中，有两个函数aFunction、aInnerFunction。
 * aFunction调用aInnerFunction。而且aFunction函数会被外部调用。
 * 情况2：aFunction不添加注解，aInnerFunction添加注解。aInnerFunction抛异常。
 *
 * 结果：两个函数对数据库的操作都不会回滚。因为内部函数@Transactional注解添加和没添加一样。
 * @Class AClass
 * @Author ZYC
 * @Date 2021/3/23 9:32
 * @Version 1.0
 **/
@Service
public class AClass3 {
    @Resource
    VoteMapper voteMapper;

    public void aFunction() {
        //delete vote
        int delete = this.voteMapper.deleteByPrimaryKey("02hawv7bdj1yph60bc0c5nipkfhx9u5f");
        aInnerFunction();
    }

    @Transactional(rollbackFor = Exception.class)
    public void aInnerFunction() {
        int delete = this.voteMapper.deleteByPrimaryKey("1ft9pwmxcpcvfc1wytpsbebrkg7qjkog");
        throw new RuntimeException("函数执行有异常!");
    }
}
