package filter;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:
 * 处理链式
 * 这里多了一个FilterChain，也就是责任链，是用于串起所有的责任对象的，它也是StudyPrepareFilter的一个子类
 * @Class FilterChain
 * @Author ZYC
 * @Date 2021/3/9 15:17
 * @Version 1.0
 **/
public class FilterChain implements StudyPrepareFilter {

    private int pos = 0;
    //如果没有表示链上所有对象都执行完毕，执行Study类的study()方法：
    private Study study;
    //链上对象
    private List<StudyPrepareFilter> studyPrepareFilterList;

    FilterChain(Study study){
        this.study = study;
    }

     public void addFilter(StudyPrepareFilter studyPrepareFilter){
        if(CollectionUtils.isEmpty(studyPrepareFilterList)){
            this.studyPrepareFilterList = new ArrayList<>();
        }
         this.studyPrepareFilterList.add(studyPrepareFilter);
     }

    @Override
    public void doFilter(PreparationList preparationList, FilterChain filterChain) {
        //判断链式对象是否执行完毕
        if(this.pos == this.studyPrepareFilterList.size()){
            this.study.doStudy();
            return;
        }
        //循环执行所有链上的对象
        StudyPrepareFilter studyPrepareFilter = this.studyPrepareFilterList.get(this.pos++);
        studyPrepareFilter.doFilter(preparationList,filterChain);
    }
}
