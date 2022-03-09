package filter;

/**
 * 功能描述:
 * 上学前准备洗头
 * @Class WashHairFilter
 * @Author ZYC
 * @Date 2021/3/9 15:30
 * @Version 1.0
 **/
public class WashHairFilter implements StudyPrepareFilter {
    @Override
    public void doFilter(PreparationList preparationList, FilterChain filterChain) {
        if(preparationList.isWashHair()){
            System.out.println("洗完头发");
        }
        filterChain.doFilter(preparationList,filterChain);
    }
}
