package filter;

/**
 * 功能描述:
 *
 * @Class FilterMain
 * @Author ZYC
 * @Date 2021/3/9 15:35
 * @Version 1.0
 **/
public class FilterMain {
    public static void main(String[] args) {
        PreparationList preparationList = new PreparationList();
        preparationList.setWashFace(true);
        preparationList.setWashHair(true);

        Study study = new Study();
        StudyPrepareFilter washFaceFilter = new WashFaceFilter();
        StudyPrepareFilter washHairFilter = new WashHairFilter();

        FilterChain filterChain = new FilterChain(study);
        filterChain.addFilter(washFaceFilter);
        filterChain.addFilter(washHairFilter);

        filterChain.doFilter(preparationList,filterChain);
    }
}
