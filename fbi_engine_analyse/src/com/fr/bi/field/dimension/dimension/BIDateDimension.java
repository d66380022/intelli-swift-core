package com.fr.bi.field.dimension.dimension;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.List;

public class BIDateDimension extends BIAbstractDimension {

<<<<<<< HEAD
=======
    private static final long serialVersionUID = 45713218397075589L;

    /**
     * 转化string
     *
     * @param v 值
     * @return 转化的string
     */
    @Override
    public String toString(Object v) {
        if (v == null || StringUtils.isEmpty(v.toString())) {
            return StringUtils.EMPTY;
        }
        return v.toString();
    }


>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIDateDimension)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return true;
    }


    @Override
    public BIKey createKey(BusinessField column) {
        return new IndexTypeKey(column.getFieldName(), group.getType());
    }

    @Override
    public DimensionCalculator createCalculator(BusinessField column, List<BITableSourceRelation> relations) {
        return new DateDimensionCalculator(this, column, relations);
    }

    @Override
    public DimensionCalculator createCalculator(BusinessField column, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        return new DateDimensionCalculator(this, column, relations, directToDimensionRelations);
    }

    private Object insertZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    @Override
    public Object getValueByType(Object data) {
        return data == null || !(data instanceof Number) ? null : Long.parseLong(data.toString());
    }
}