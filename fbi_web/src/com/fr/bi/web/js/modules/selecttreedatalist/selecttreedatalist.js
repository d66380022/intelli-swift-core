/**
 * Created by zcf on 2016/12/20.
 */
BI.SelectTreeDataList = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectTreeDataList.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        BI.SelectTreeDataList.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.treeList = BI.createWidget({
            type: "bi.multi_tree_list",
            itemsCreator: function (op, callback) {
                var data = BI.extend({
                    floors: BI.size(BI.Utils.getAllDimensionIDs(o.wId))
                }, op);
                BI.Utils.getWidgetDataByID(o.wId, {
                    success: function (jsonData) {
                        callback(jsonData);
                    }
                }, {tree_options: data})
            }
        });
        this.treeList.on(BI.MultiTreeList.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectTreeDataList.EVENT_CHANGE);
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.treeList
            }]
        });
    },

    resize: function () {
        this.treeList.resize();
    },

    setEnable: function (v) {
        this.treeList.setEnable(v);
    },

    getValue: function () {
        return this.treeList.getValue();
    },

    populate: function () {
        var o = this.options;
        this.treeList.setValue(BI.Utils.getWidgetValueByID(o.wId));
        this.treeList.populate.apply(this.treeList, arguments);
    }
});
BI.SelectTreeDataList.EVENT_CHANGE = "SelectTreeDataList.EVENT_CHANGE";
$.shortcut("bi.select_tree_data_list", BI.SelectTreeDataList);