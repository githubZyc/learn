package filter;

/**
 * 功能描述:
 *
 * @Class WashFaceFilter
 * @Author ZYC
 * @Date 2021/3/9 15:33
 * @Version 1.0
 **/
public class WashFaceFilter implements StudyPrepareFilter {
    @Override
    public void doFilter(PreparationList preparationList, FilterChain filterChain) {
        if(preparationList.isWashFace()){
            System.out.println("洗完脸");
        }
        filterChain.doFilter(preparationList,filterChain);
    }
}
