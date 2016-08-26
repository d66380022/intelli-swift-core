/**
 * Created by fay on 2016/8/1.
 */
BI.BubbleFilterSelectField = BI.inherit(BI.Widget, {
    _constant: {
        DIMENSION_FIELD: 1,
        X_Y_FIELD: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.BubbleFilterSelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-label-filter-select-field"
        });
    },

    _init: function () {
        BI.BubbleFilterSelectField.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.simple_select_data_service",
            element: this.element,
            isDefaultInit: true,
            tablesCreator: function () {
                return [{
                    id: self._constant.DIMENSION_FIELD,
                    type: "bi.dimension_select_data_level0_node",
                    text: BI.i18nText("BI-Dimension"),
                    value: BI.i18nText("BI-Dimension"),
                    isParent: true,
                    fontType: BI.DimensionSelectDataLevel0Node.SERIES,
                    open: true
                }, {
                    id: self._constant.X_Y_FIELD,
                    type: "bi.dimension_select_data_level0_node",
                    text: BI.i18nText("BI-Uppercase_X_Axis") + "/" + BI.i18nText("BI-Uppercase_Y_Axis"),
                    value: BI.i18nText("BI-Uppercase_X_Axis") + "/" + BI.i18nText("BI-Uppercase_Y_Axis"),
                    isParent: true,
                    fontType: BI.DimensionSelectDataLevel0Node.CLASSIFY,
                    open: true
                }]
            },
            fieldsCreator: function (tableId) {
                var view = BI.Utils.getWidgetViewByID(BI.Utils.getWidgetIDByDimensionID(o.dId));
                var categories = view[10000];
                var y = view[30000];
                var x = view[40000];
                var bubbleSize = view[50000];
                var result = [];
                if (tableId === self._constant.DIMENSION_FIELD) {
                    BI.each(categories, function (i, dId) {
                        if (!BI.Utils.isDimensionUsable(dId)) {
                            return;
                        }
                        result.push({
                            id: dId,
                            pId: self._constant.DIMENSION_FIELD,
                            type: "bi.select_data_level0_item",
                            fieldType: BI.Utils.getFieldTypeByDimensionID(dId),
                            text: BI.Utils.getDimensionNameByID(dId),
                            title: BI.Utils.getDimensionNameByID(dId),
                            value: dId
                        });
                    });
                } else {
                    result.push({
                        id: BICst.DATACOLUMN.XANDYANDSIZE,
                        pId: self._constant.X_Y_FIELD,
                        type: "bi.select_data_level0_item",
                        text: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Bubble_Size"),
                        title: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Bubble_Size"),
                        fieldType: BICst.DATACOLUMN.XANDYANDSIZE
                    });
                    result.push({
                        id: BICst.DATACOLUMN.XANDY,
                        pId: self._constant.X_Y_FIELD,
                        type: "bi.select_data_level0_item",
                        text: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis"),
                        title: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis"),
                        fieldType: BICst.DATACOLUMN.XANDY
                    });
                    BI.each(x, function (i, dId) {
                        if (!BI.Utils.isDimensionUsable(dId)) {
                            return;
                        }
                        result.push({
                            id: dId,
                            pId: self._constant.X_Y_FIELD,
                            type: "bi.select_data_level0_item",
                            fieldType: BI.Utils.getFieldTypeByDimensionID(dId),
                            text: BI.i18nText("BI-Uppercase_X_Axis"),
                            title: BI.i18nText("BI-Uppercase_X_Axis"),
                            value: dId
                        });
                    });
                    BI.each(y, function (i, dId) {
                        if (!BI.Utils.isDimensionUsable(dId)) {
                            return;
                        }
                        result.push({
                            id: dId,
                            pId: self._constant.X_Y_FIELD,
                            type: "bi.select_data_level0_item",
                            fieldType: BI.Utils.getFieldTypeByDimensionID(dId),
                            text: BI.i18nText("BI-Uppercase_Y_Axis"),
                            title: BI.i18nText("BI-Uppercase_Y_Axis"),
                            value: dId
                        });
                    });
                    BI.each(bubbleSize, function (i, dId) {
                        if (!BI.Utils.isDimensionUsable(dId)) {
                            return;
                        }
                        result.push({
                            id: dId,
                            pId: self._constant.X_Y_FIELD,
                            type: "bi.select_data_level0_item",
                            fieldType: BI.Utils.getFieldTypeByDimensionID(dId),
                            text: BI.i18nText("BI-Bubble_Size"),
                            title: BI.i18nText("BI-Bubble_Size"),
                            value: dId
                        });
                    });
                }
                return result;
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.BubbleFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
    }
});
BI.BubbleFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.bubble_filter_select_field", BI.BubbleFilterSelectField);