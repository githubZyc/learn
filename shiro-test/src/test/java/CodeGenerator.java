import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.haigrid.media.util.Digests;
import org.junit.Test;


public class CodeGenerator {
    @Test
    public void main2(){
        String admin = Digests.entryptPassword("admin");
        System.out.println(admin);
    }

    @Test
    public void main1() {
        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();
//        String projectPath = System.getProperty("user.dir");
//        System.out.println(projectPath);

        gc.setOutputDir("/Volumes/files/workspace/learn/shiro-test/src/main/java");
//        gc.setOutputDir("F:\\idea\\haigezhuoyue\\media\\src\\main\\java");
        gc.setAuthor("ZhengYanChuang");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setFileOverride(true); //重新生成时文件是否覆盖
        /*
         * mp生成service层代码，默认接口名称第一个字母有 I
         * UcenterService
         * */
        gc.setServiceName("%sService"); //去掉Service接口的首字母I
        gc.setIdType(IdType.UUID); //主键策略
        gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
        //gc.setSwagger2(false);//开启Swagger2模式
        gc.setSwagger2(true);//开启Swagger2模式
        mpg.setGlobalConfig(gc);
        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://rm-2ze3kg3wjwkme9hv8fo.mysql.rds.aliyuncs.com:3306/media_test?serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("media_test");
        dsc.setPassword("haige1234!@#$");

        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);
        // 4、包配置
        PackageConfig pc = new PackageConfig();
//        pc.setModuleName("hgNewsMedia"); //模块名
        pc.setParent("com.haigrid.media");
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);
        // 5、策略配置C
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude("sys_user","sys_user_role",
                "sys_role","sys_menu","sys_office","sys_role_menu","sys_role_office");
        //排除
//        strategy.setExclude("xxxx");

        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        strategy.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
        strategy.setRestControllerStyle(true); //restful api风格控制器
        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
        mpg.setStrategy(strategy);
        // 6、执行
        mpg.execute();
    }
}
