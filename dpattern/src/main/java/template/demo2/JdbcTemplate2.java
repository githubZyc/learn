package template.demo2;

/**
 * 功能描述:
 *
 * @Class JdbcTemplate2
 * @Author ZYC
 * @Date 2021/4/26 10:37
 * @Version 1.0
 **/
public abstract class JdbcTemplate2 {
    public Object query(StatementCallback stmt) {
         return execute(stmt);
    }

    public final Object execute(StatementCallback statementCallback){
        //调用接口方法
        String sql="";
        return statementCallback.doInStatement(sql);
    }
}
