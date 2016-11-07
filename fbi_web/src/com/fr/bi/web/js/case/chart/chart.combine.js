/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CombineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart",
            items: [],
            xAxis: [{type: "category"}],
            yAxis: [{type: "value"}],
            types: [[], []],
            popupItemsGetter: BI.emptyFn,
            formatConfig: function (config) {
                return config;
            }
        })
    },

    _init: function () {
        BI.CombineChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //图可配置属性
        this.CombineChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.CombineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, obj);
        });

        if (BI.isNotEmptyArray(o.items)) {
            this.populate(o.items);
        }
    },

    _formatItems: function (items) {
        var result = [], self = this, o = this.options;
        var yAxisIndex = 0;
        BI.each(items, function (i, belongAxisItems) {
            var combineItems = BI.ChartCombineFormatItemFactory.combineItems(o.types[i], belongAxisItems);
            BI.each(combineItems, function (j, axisItems) {
                if (BI.isArray(axisItems)) {
                    result = BI.concat(result, axisItems);
                } else {
                    result.push(BI.extend(axisItems, {"yAxis": yAxisIndex}));
                }
            });
            if (BI.isNotEmptyArray(combineItems)) {
                yAxisIndex++;
            }
        });
        var config = BI.ChartCombineFormatItemFactory.combineConfig();
        config.plotOptions.click = function () {
            var data = this.options;
            data.toolTipRect = this.getTooltipRect();
            var items = o.popupItemsGetter(data);
            if (items && items.length > 0) {
                self._createPopup(items, data.toolTipRect, data);
            }
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, data);
        };
        return [result, config];
    },

    _createPopup: function (items, rect, opt) {
        var combo = BI.createWidget({
            type: "bi.combo",
            direction: "bottom",
            popup: {
                el: BI.createWidget({
                    type: "bi.vertical",
                    cls: "bi-linkage-list",
                    items: BI.map(items, function (i, item) {
                        return {
                            type: "bi.text_button",
                            cls: "bi-linkage-list-item",
                            textAlign: "left",
                            height: 30,
                            handler: function () {
                                self.fireEvent(BI.CombineChart.EVENT_ITEM_CLICK, BI.extend({}, item, opt));
                                combo.destroy();
                            },
                            lgap: 10
                        }
                    }),
                    width: rect.width
                })
            },
            width: 0
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: combo,
                top: rect.y,
                left: rect.x
            }]
        });
        combo.showView();
    },

    setTypes: function (types) {
        this.options.types = types || [[]];
    },

    populate: function (items, types) {
        var o = this.options;
        if (BI.isNotNull(types)) {
            this.setTypes(types);
        }
        var opts = this._formatItems(items);
        BI.extend(opts[1], {
            xAxis: o.xAxis,
            yAxis: o.yAxis
        });
        var result = o.formatConfig(opts[1], opts[0]);
        this.CombineChart.populate(result[0], result[1]);
    },

    resize: function () {
        this.CombineChart.resize();
    },

    magnify: function () {
        this.CombineChart.magnify();
    }
});
BI.CombineChart.EVENT_CHANGE = "EVENT_CHANGE";
BI.CombineChart.EVENT_ITEM_CLICK = "EVENT_ITEM_CLICK";
$.shortcut('bi.combine_chart', BI.CombineChart);