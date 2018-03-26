package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.control.time.QuarterControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.DimensionTypeGroup;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.time.BIQuarterResult;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/3/26.
 */
public class QuarterControlWidgetAdaptor {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BIMonthControlResultAdaptor.class);

    public static BIQuarterResult calculate(QuarterControlWidget widget) {
        try {
            FineDimension dimension = widget.getDimensionList().get(0);
            //设置下年分组
            dimension.setGroup(new DimensionTypeGroup());
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());
            List<Long> yearValues = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            List<Integer> years = new ArrayList<Integer>();
            for (Long v : yearValues) {
                years.add(v.intValue());
            }
            //设置下季度分组
            dimension.setGroup(new DimensionTypeGroup());
            List<Long> monthValues = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            List<Integer> months = new ArrayList<Integer>();
            for (Long v : monthValues) {
                months.add(v.intValue());
            }
            return new QuarterResult(years, months);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    static class QuarterResult implements BIQuarterResult {
        private List<Integer> years;
        private List<Integer> quarters;

        public QuarterResult(List years, List quarters) {
            this.years = years;
            this.quarters = quarters;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.QUARTER;
        }


        @Override
        public List<Integer> getQuarters() {
            return quarters;
        }

        @Override
        public List<Integer> getYears() {
            return years;
        }
    }
}
