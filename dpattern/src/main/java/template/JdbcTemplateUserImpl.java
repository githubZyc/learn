package template;

/**
 * 功能描述:
 *
 * @Class JdbcTemplateUserImpl
 * @Author ZYC
 * @Date 2021/4/26 10:20
 * @Version 1.0
 **/
public class JdbcTemplateUserImpl extends JdbcTemplate {
    //子类实现方法
    @Override
    protected Object doInStatement(String sql) {
        System.out.println("执行用户查询操作：" + sql);
        return "{username:1,age:10}";
    }
}
