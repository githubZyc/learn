package template.demo2;

/**
 * 功能描述:
 *
 * @Class StatementCallback
 * @Author ZYC
 * @Date 2021/4/26 10:36
 * @Version 1.0
 **/
public interface StatementCallback {
    Object doInStatement(String sql);
}
