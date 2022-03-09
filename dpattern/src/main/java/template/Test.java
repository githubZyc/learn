package template;

/**
 * 功能描述:
 *
 * @Class Test
 * @Author ZYC
 * @Date 2021/4/26 10:22
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args) {
        JdbcTemplate jdbcTemplate = new JdbcTemplateUserImpl();
        String sql = "select * from User";
        Object exec = jdbcTemplate.exec(sql);
        System.out.println("执行返回结果：" + exec);


        JdbcTemplate jdbcTeachtTemplate = new JdbcTemplateTeacherImpl();
        String sql1 = "select * from Teacher";
        Object exec1 = jdbcTeachtTemplate.exec(sql1);
        System.out.println("执行返回结果：" + exec1);
    }
}
