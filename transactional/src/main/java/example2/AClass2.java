package example2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 功能描述:
 * 同一个类AClass中，有两个函数aFunction、aInnerFunction。
 * aFunction调用aInnerFunction。而且aFunction函数会被外部调用。
 * 情况1：两个函数都添加了@Transactional注解。aInnerFunction抛异常。
 *
 * 结果：两个函数对数据库操作都会回滚。
 *       因为同一个类中函数相互调用的时候，内部函数添加@Transactional注解无效。
 *      Transactional注解只有外部调用才有效。
 * @Class AClass
 * @Author ZYC
 * @Date 2021/3/23 9:32
 * @Version 1.0
 **/
@Service
public class AClass2 {
    @Resource
    BaseMapper voteMapper;

    @Transactional(rollbackFor = Exception.class)
    public void aFunction() {
        //delete vote
        int delete = this.voteMapper.deleteById("02hawv7bdj1yph60bc0c5nipkfhx9u5f");
        aInnerFunction(); // 调用内部没有添加@Transactional注解的函数
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void aInnerFunction() {
        int delete = this.voteMapper.deleteById("0cul56o9o0v452aaewc4lc5qjw8udfuu");
        throw new RuntimeException("函数执行有异常!");
    }
}
