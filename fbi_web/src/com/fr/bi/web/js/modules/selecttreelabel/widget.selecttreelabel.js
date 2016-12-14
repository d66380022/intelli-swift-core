/**
 * Created by fay on 2016/10/11.
 */
BI.SelectTreeLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectTreeLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-tree-label",
            wId: ""
        })
    },

    _init: function () {
        BI.SelectTreeLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.treeLabel = BI.createWidget({
            type: "bi.tree_label",
            element: this.element,
            itemsCreator: function (op, callback) {
                var data = {};
                data.floors = op.floor;
                data.parentValues = op.parentValues;
                if (BI.isEmptyObject(op)) {
                    callback({});
                } else {
                    BI.Utils.getWidgetDataByID(o.wId, {
                        success: function (jsonData) {
                            callback(jsonData);
                        }
                    }, {tree_options: data})
                }
            }
        });

        this.treeLabel.on(BI.TreeLabel.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectTreeLabel.EVENT_CONFIRM, arguments);
        });
    },

    setValue: function (v) {
        var self = this, o = this.options;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        if (BI.isEmptyArray(dimensions)) {
            return self.treeLabel.populate({
                items: [],
                titles: [],
                selectedValue: v
            });
        }
        var titles = [];
        BI.each(dimensions, function (idx, dId) {
            var temp = BI.Utils.getDimensionNameByID(dId);
            titles.push({
                text: temp + BI.i18nText("BI-Colon"),
                title: temp
            })
        });
        var data = {};
        data.floors = 0;
        data.selectedValues = v;
        data.parentValues = [{
            id: "",
            value: []
        }];
        BI.Utils.getWidgetDataByID(o.wId, {
            success: function (jsonData) {
                self.treeLabel.populate({
                    items: jsonData.items,
                    titles: titles,
                    selectedValue: v
                });
            }
        }, {tree_options: data});
    },

    getValue: function () {
        return this.treeLabel.getValue();
    }
});
BI.SelectTreeLabel.EVENT_CONFIRM = "SelectTreeLabel.EVENT_CONFIRM";
$.shortcut('bi.select_tree_label', BI.SelectTreeLabel);