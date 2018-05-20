package com.fr.swift.adaptor.widget;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.internalimp.bean.dashboard.widget.chart.VanChartWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.number.NumberWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.number.SingleSliderWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.string.ListLabelWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.string.StringControlWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.string.StringListControlWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.string.StringSelectedValue;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.DateControlBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.DateIntervalControlBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.DatePaneControlBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.MonthControlBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.QuarterControlWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.YearControlBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.time.YearMonthIntervalWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.tree.TreeLabelWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.tree.TreeListBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.control.tree.TreeWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.detail.DetailWidgetBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.WidgetDimensionBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.CrossTableBean;
import com.finebi.conf.internalimp.bean.dashboard.widget.table.TableWidgetBean;
import com.finebi.conf.internalimp.bean.filter.AbstractFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraAndFilterBean;
import com.finebi.conf.internalimp.bean.filter.GeneraOrFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.date.DateEqualFilterBean;
import com.finebi.conf.internalimp.bean.filter.number.NumberBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.string.StringBelongFilterBean;
import com.finebi.conf.internalimp.bean.filter.visiter.FilterBeanToFilterVisitor;
import com.finebi.conf.internalimp.bean.filtervalue.date.DateRangeValueBean;
import com.finebi.conf.internalimp.bean.filtervalue.number.NumberValue;
import com.finebi.conf.internalimp.bean.filtervalue.string.StringBelongFilterValueBean;
import com.finebi.conf.internalimp.filter.date.DateBelongFilter;
import com.finebi.conf.internalimp.filter.date.DateEqualFilter;
import com.finebi.conf.internalimp.filter.number.NumberBelongFilter;
import com.finebi.conf.internalimp.filter.string.StringBelongFilter;
import com.finebi.conf.structure.bean.dashboard.visitor.WidgetBeanVisitor;
import com.finebi.conf.structure.bean.filter.DateFilterBean;
import com.finebi.conf.structure.bean.filter.FilterBean;
import com.finebi.conf.structure.filter.FineFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pony on 2018/5/19.
 */
public class WidgetFilterVisitor implements WidgetBeanVisitor<List<FineFilter>> {
    @Override
    public List<FineFilter> visit(TableWidgetBean tableWidgetBean) throws Exception {
        return null;
    }

    @Override
    public List<FineFilter> visit(VanChartWidgetBean vanChartWidgetBean) throws Exception {
        return null;
    }

    @Override
    public List<FineFilter> visit(DetailWidgetBean detailWidgetBean) {
        return null;
    }

    @Override
    public List<FineFilter> visit(CrossTableBean crossTableBean) throws Exception {
        return null;
    }


    public List<FineFilter> visit(StringControlWidgetBean widget) throws Exception {
        String fieldId = widget.getDimensions().values().iterator().next().getFieldId();
        return visitStringSelectValue(widget.getValue(), widget.getDimensions().values(), fieldId);
    }

    private List<FineFilter> visitStringSelectValue(StringSelectedValue selectedValue, Collection<WidgetDimensionBean> dimensions, String fieldId) throws Exception {
        List<FineFilter> filters = new ArrayList<FineFilter>();
        StringBelongFilter filter = new StringBelongFilter();
        StringBelongFilterBean filterBean = new StringBelongFilterBean();
        StringBelongFilterValueBean filterValueBean = new StringBelongFilterValueBean();
        filterValueBean.setType(selectedValue.getType());
        filterValueBean.setAssist(selectedValue.getAssist());
        filterValueBean.setValue(selectedValue.getValue());
        filterBean.setFilterValue(filterValueBean);
        filterBean.setFieldId(fieldId);
        filter.setValue(filterBean);
        filters.add(filter);
        if (selectedValue.getType() == BICommonConstants.SELECTION_TYPE.ALL) {
            addWidgetFilter(dimensions, filters);
        }
        return filters;
    }

    @Override
    public List<FineFilter> visit(SingleSliderWidgetBean singleSliderWidgetBean) throws Exception {
        String fieldId = singleSliderWidgetBean.getDimensions().values().iterator().next().getFieldId();
        return visitNumberValue(singleSliderWidgetBean.getValue(), fieldId);
    }

    @Override
    public List<FineFilter> visit(YearControlBean yearControlBean) throws Exception {
        String fieldId = yearControlBean.getDimensions().values().iterator().next().getFieldId();
        return visitDateEqualValue(yearControlBean.getValue(), fieldId);
    }

    private List<FineFilter> visitDateEqualValue(DateFilterBean selectedValue, String fieldId) throws Exception {
        List<FineFilter> filters = new ArrayList<FineFilter>();
        DateEqualFilter filter = new DateEqualFilter();
        DateEqualFilterBean filterBean = new DateEqualFilterBean();
        filterBean.setFieldId(fieldId);
        filterBean.setFilterValue(selectedValue);
        filter.setValue(filterBean);
        filters.add(filter);
        return filters;
    }


    private List<FineFilter> visitDateRangeValValue(DateRangeValueBean selectedValue, String fieldId) throws Exception {
        List<FineFilter> filters = new ArrayList<FineFilter>();
        DateBelongFilter filter = new DateBelongFilter();
        DateBelongFilterBean filterBean = new DateBelongFilterBean();
        filterBean.setFieldId(fieldId);
        filterBean.setFilterValue(selectedValue);
        filter.setValue(filterBean);
        filters.add(filter);
        return filters;
    }

    @Override
    public List<FineFilter> visit(MonthControlBean monthControlBean) throws Exception {
        String fieldId = monthControlBean.getDimensions().values().iterator().next().getFieldId();
        return visitDateEqualValue(monthControlBean.getValue(), fieldId);
    }

    @Override
    public List<FineFilter> visit(QuarterControlWidgetBean quarterControlWidgetBean) throws Exception {
        String fieldId = quarterControlWidgetBean.getDimensions().values().iterator().next().getFieldId();
        return visitDateEqualValue(quarterControlWidgetBean.getValue(), fieldId);
    }

    @Override
    public List<FineFilter> visit(TreeWidgetBean treeWidgetBean) throws Exception {
        return null;
    }

    @Override
    public List<FineFilter> visit(DateControlBean dateControlBean) throws Exception {
        String fieldId = dateControlBean.getDimensions().values().iterator().next().getFieldId();
        return visitDateEqualValue(dateControlBean.getValue(), fieldId);
    }

    @Override
    public List<FineFilter> visit(DateIntervalControlBean dateIntervalControlBean) throws Exception {
        String fieldId = dateIntervalControlBean.getDimensions().values().iterator().next().getFieldId();
        return visitDateRangeValValue(dateIntervalControlBean.getValue(), fieldId);
    }

    @Override
    public List<FineFilter> visit(DatePaneControlBean datePaneControlBean) throws Exception {
        String fieldId = datePaneControlBean.getDimensions().values().iterator().next().getFieldId();
        return visitDateEqualValue(datePaneControlBean.getValue(), fieldId);
    }

    @Override
    public List<FineFilter> visit(YearMonthIntervalWidgetBean yearMonthIntervalWidgetBean) throws Exception {
        String fieldId = yearMonthIntervalWidgetBean.getDimensions().values().iterator().next().getFieldId();
        return visitDateRangeValValue(yearMonthIntervalWidgetBean.getValue(), fieldId);
    }

    @Override
    public List<FineFilter> visit(ListLabelWidgetBean listLabelWidgetBean) throws Exception {
        String fieldId = listLabelWidgetBean.getDimensions().values().iterator().next().getFieldId();
        return visitStringSelectValue(listLabelWidgetBean.getValue(), listLabelWidgetBean.getDimensions().values(), fieldId);
    }

    @Override
    public List<FineFilter> visit(TreeLabelWidgetBean treeLabelWidgetBean) throws Exception {
        return null;
    }

    @Override
    public List<FineFilter> visit(TreeListBean treeListBean) throws Exception {
        return null;
    }

    @Override
    public List<FineFilter> visit(StringListControlWidgetBean stringListControlWidgetBean) throws Exception {
        String fieldId = stringListControlWidgetBean.getDimensions().values().iterator().next().getFieldId();
        return visitStringSelectValue(stringListControlWidgetBean.getValue(), stringListControlWidgetBean.getDimensions().values(), fieldId);
    }

    @Override
    public List<FineFilter> visit(NumberWidgetBean numberWidgetBean) throws Exception {
        String fieldId = numberWidgetBean.getDimensions().values().iterator().next().getFieldId();
        return visitNumberValue(numberWidgetBean.getValue(), fieldId);
    }

    private List<FineFilter> visitNumberValue(NumberValue selectedValue, String fieldId) throws Exception {
        List<FineFilter> filters = new ArrayList<FineFilter>();
        NumberBelongFilter filter = new NumberBelongFilter();
        NumberBelongFilterBean filterBean = new NumberBelongFilterBean();
        NumberValue filterValueBean = new NumberValue();
        filterValueBean.setMax(selectedValue.getMax());
        filterValueBean.setClosemax(selectedValue.isClosemax());
        filterValueBean.setMin(selectedValue.getMin());
        filterValueBean.setClosemin(selectedValue.isClosemin());
        filterBean.setFilterValue(filterValueBean);
        filterBean.setFieldId(fieldId);
        filter.setValue(filterBean);
        filters.add(filter);
        return filters;
    }


    private void addWidgetFilter(Collection<WidgetDimensionBean> dimensions, List<FineFilter> filters) throws Exception {
        FilterBeanToFilterVisitor visitor = new FilterBeanToFilterVisitor();
        for (WidgetDimensionBean dimensionBean : dimensions) {
            FilterBean filterBean = dimensionBean.getFilter();
            setFilterBeanId(filterBean, dimensionBean.getFieldId());
            if (dimensionBean.getFilter() != null) {
                filters.add(filterBean.accept(visitor));
            }
        }
    }

    private void setFilterBeanId(FilterBean filterBean, String fieldId) {
        int type = filterBean.getFilterType();
        if (type == BICommonConstants.ANALYSIS_FILTER_TYPE.OR || type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND) {
            List<FilterBean> beans = type == BICommonConstants.ANALYSIS_FILTER_TYPE.AND ?
                    ((GeneraAndFilterBean) filterBean).getFilterValue() : ((GeneraOrFilterBean) filterBean).getFilterValue();
            for (FilterBean bean : beans) {
                setFilterBeanId(bean, fieldId);
            }
        }
        ((AbstractFilterBean) filterBean).setFieldId(fieldId);
    }

}
