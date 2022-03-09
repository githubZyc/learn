package template;
/**
 * 功能描述:
 *
 * @Class JdbcTemplate
 * @Author ZYC
 * @Date 2021/4/26 10:12
 * @Version 1.0
 **/
public abstract class JdbcTemplate {

    //template method
    public final Object exec(String sql){
        System.out.println("执行sql" + sql);
        //调用子类方法
        Object result = doInStatement(sql);
        return result;
    }

    //子类实现方法
    protected abstract Object doInStatement(String sql);
}
