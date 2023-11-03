package template.demo2;

/**
 * 功能描述:
 *
 * @Class JdbcTemplateTeacherImpl
 * @Author ZYC
 * @Date 2021/4/26 10:25
 * @Version 1.0
 **/
public class JdbcTemplateTeacherImpl extends JdbcTemplate {
    //子类实现方法
    @Override
    protected Object doInStatement(String sql) {
        System.out.println("执行老师查询操作：" + sql);
        return "{username:王老师,age:50}";
    }
}
