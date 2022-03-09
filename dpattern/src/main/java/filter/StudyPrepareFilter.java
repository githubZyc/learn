package filter;

/**
 * 学习的准备过滤器
 */
public interface StudyPrepareFilter {
    void doFilter(PreparationList preparationList, FilterChain filterChain);
}
