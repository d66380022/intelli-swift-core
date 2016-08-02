/**
 * 图表控件
 * @class BI.AreaChart
 * @extends BI.Widget
 */
BI.AreaChart = BI.inherit(BI.AbstractChart, {

    _defaultConfig: function () {
        return BI.extend(BI.AreaChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-area-chart"
        })
    },

    _init: function () {
        BI.AreaChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.xAxis = [{
            type: "category",
            title: {
                style: {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""}
            },
            labelStyle: {
                "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
            },
            position: "bottom",
            gridLineWidth: 0
        }];
        this.combineChart = BI.createWidget({
            type: "bi.combine_chart",
            xAxis: this.xAxis,
            formatConfig: BI.bind(this._formatConfig, this),
            element: this.element
        });
        this.combineChart.on(BI.CombineChart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.AxisChart.EVENT_CHANGE, obj);
        });
    },

    _formatConfig: function(config, items){
        var self = this, o = this.options;
        config.colors = this.config.chart_color;
        config.style = formatChartStyle();
        formatChartLineStyle();
        formatCordon();
        switch (this.config.chart_legend){
            case BICst.CHART_LEGENDS.BOTTOM:
                config.legend.enabled = true;
                config.legend.position = "bottom";
                config.legend.maxHeight = self.constants.LEGEND_HEIGHT;
                break;
            case BICst.CHART_LEGENDS.RIGHT:
                config.legend.enabled = true;
                config.legend.position = "right";
                break;
            case BICst.CHART_LEGENDS.NOT_SHOW:
            default:
                config.legend.enabled = false;
                break;
        }
        config.plotOptions.dataLabels.enabled = this.config.show_data_label;
        config.dataSheet.enabled = this.config.show_data_table;
        config.xAxis[0].showLabel = !config.dataSheet.enabled;
        config.zoom.zoomTool.visible = this.config.show_zoom;
        if(this.config.show_zoom === true){
            delete config.dataSheet;
            delete config.zoom.zoomType;
        }
        config.yAxis = this.yAxis;

        BI.each(config.yAxis, function(idx, axis){
            switch (axis.axisIndex){
                case self.constants.LEFT_AXIS:
                    axis.reversed = self.config.left_y_axis_reversed;
                    axis.formatter = formatTickInXYaxis(self.config.left_y_axis_style, self.constants.LEFT_AXIS);
                    self.formatNumberLevelInYaxis(config, items, self.config.left_y_axis_number_level, idx, axis.formatter);
                    axis.title.text = getXYAxisUnit(self.config.left_y_axis_number_level, self.constants.LEFT_AXIS);
                    axis.title.text = self.config.show_left_y_axis_title === true ? self.config.left_y_axis_title + axis.title.text : axis.title.text;
                    axis.gridLineWidth = self.config.show_grid_line === true ? 1 : 0;
                    axis.title.rotation = self.constants.ROTATION;
                    break;
                case self.constants.RIGHT_AXIS:
                    axis.reversed = self.config.right_y_axis_reversed;
                    axis.formatter = formatTickInXYaxis(self.config.right_y_axis_style, self.constants.RIGHT_AXIS);
                    self.formatNumberLevelInYaxis(config, items, self.config.right_y_axis_number_level, idx, axis.formatter);
                    axis.title.text = getXYAxisUnit(self.config.right_y_axis_number_level, self.constants.RIGHT_AXIS);
                    axis.title.text = self.config.show_right_y_axis_title === true ? self.config.right_y_axis_title + axis.title.text : axis.title.text;
                    axis.gridLineWidth = self.config.show_grid_line === true ? 1 : 0;
                    axis.title.rotation = self.constants.ROTATION;
                    break;
                case self.constants.RIGHT_AXIS_SECOND:
                    axis.reversed = self.config.right_y_axis_second_reversed;
                    axis.formatter = formatTickInXYaxis(self.config.right_y_axis_second_style, self.constants.RIGHT_AXIS_SECOND);
                    self.formatNumberLevelInYaxis(config, items, self.config.right_y_axis_second_number_level, idx, axis.formatter);
                    axis.title.text = getXYAxisUnit(self.config.right_y_axis_second_number_level, self.constants.RIGHT_AXIS_SECOND);
                    axis.title.text = self.config.show_right_y_axis_second_title === true ? self.config.right_y_axis_second_title + axis.title.text : axis.title.text;
                    axis.gridLineWidth = self.config.show_grid_line === true ? 1 : 0;
                    axis.title.rotation = self.constants.ROTATION;
                    break;
            }
        });

        config.xAxis[0].title.text = this.config.x_axis_title;
        config.xAxis[0].labelRotation = this.config.text_direction;
        config.xAxis[0].title.text = this.config.show_x_axis_title === true ? config.xAxis[0].title.text : "";
        config.xAxis[0].title.align = "center";
        config.xAxis[0].gridLineWidth = this.config.show_grid_line === true ? 1 : 0;
        config.chartType = "area";

        //为了给数据标签加个%,还要遍历所有的系列，唉
        if(config.plotOptions.dataLabels.enabled === true){
            BI.each(items, function(idx, item){
                var isNeedFormatDataLabel = false;
                switch (config.yAxis[item.yAxis].axisIndex) {
                    case self.constants.LEFT_AXIS:
                        if(self.config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                            isNeedFormatDataLabel = true;
                        }
                        break;
                    case self.constants.RIGHT_AXIS:
                        if(self.config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                            isNeedFormatDataLabel = true;
                        }
                        break;
                }
                if(isNeedFormatDataLabel === true){
                    item.dataLabels = {
                        "style": "{fontFamily:Microsoft YaHei, color: #808080, fontSize: 12pt}",
                        "align": "outside",
                        enabled: true,
                        formatter: {
                            identifier: "${VALUE}",
                            valueFormat: config.yAxis[item.yAxis].formatter
                        }
                    };
                }
            });
        }

        return [items, config];

        function formatChartStyle(){
            switch (self.config.chart_style) {
                case BICst.CHART_STYLE.STYLE_GRADUAL:
                    return "gradual";
                case BICst.CHART_STYLE.STYLE_NORMAL:
                default:
                    return "normal";
            }
        }

        function formatCordon(){
            BI.each(self.config.cordon, function(idx, cor){
                if(idx === 0 && self.xAxis.length > 0){
                    var magnify = calcMagnify(self.config.x_axis_number_level);
                    self.xAxis[0].plotLines = BI.map(cor, function(i, t){
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""},
                                "text": t.text,
                                "align": "top"
                            }
                        });
                    });
                }
                if(idx > 0 && self.yAxis.length >= idx){
                    var magnify = 1;
                    switch (idx - 1) {
                        case self.constants.LEFT_AXIS:
                            magnify = calcMagnify(self.config.left_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS:
                            magnify = calcMagnify(self.config.right_y_axis_number_level);
                            break;
                        case self.constants.RIGHT_AXIS_SECOND:
                            magnify = calcMagnify(self.config.right_y_axis_second_number_level);
                            break;
                    }
                    self.yAxis[idx - 1].plotLines = BI.map(cor, function(i, t){
                        return BI.extend(t, {
                            value: t.value.div(magnify),
                            width: 1,
                            label: {
                                "style": {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""},
                                "text": t.text,
                                "align": "left"
                            }
                        });
                    });
                }
            })
        }

        function formatChartLineStyle(){
            switch (self.config.chart_line_type) {
                case BICst.CHART_SHAPE.RIGHT_ANGLE:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = true;
                    break;
                case BICst.CHART_SHAPE.CURVE:
                    config.plotOptions.curve = true;
                    config.plotOptions.step = false;
                    break;
                case BICst.CHART_SHAPE.NORMAL:
                default:
                    config.plotOptions.curve = false;
                    config.plotOptions.step = false;
                    break;
            }
        }

        function calcMagnify(type){
            var magnify = 1;
            switch (type) {
                case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                    magnify = 1;
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                    magnify = 10000;
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                    magnify = 1000000;
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                    magnify = 100000000;
                    break;
            }
            return magnify;
        }

        function getXYAxisUnit(numberLevelType, position){
            var unit = "";
            switch (numberLevelType) {
                case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    unit = "";
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                    unit = BI.i18nText("BI-Wan");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                    unit = BI.i18nText("BI-Million");
                    break;
                case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                    unit = BI.i18nText("BI-Yi");
                    break;
            }
            if(position === self.constants.X_AXIS){
                self.config.x_axis_unit !== "" && (unit = unit + self.config.x_axis_unit)
            }
            if(position === self.constants.LEFT_AXIS){
                self.config.left_y_axis_unit !== "" && (unit = unit + self.config.left_y_axis_unit)
            }
            if(position === self.constants.RIGHT_AXIS){
                self.config.right_y_axis_unit !== "" && (unit = unit + self.config.right_y_axis_unit)
            }
            return unit === "" ? unit : "(" + unit + ")";
        }

        function formatTickInXYaxis(type, position){
            var formatter = '#.##';
            switch (type) {
                case self.constants.NORMAL:
                    formatter = '#.##';
                    break;
                case self.constants.ZERO2POINT:
                    formatter = '#0';
                    break;
                case self.constants.ONE2POINT:
                    formatter = '#0.0';
                    break;
                case self.constants.TWO2POINT:
                    formatter = '#0.00';
                    break;
            }
            if(position === self.constants.LEFT_AXIS){
                if(self.config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            if(position === self.constants.RIGHT_AXIS){
                if(self.config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT){
                    if(type === self.constants.NORMAL){
                        formatter = '#0%'
                    }else{
                        formatter += '%';
                    }
                }
            }
            return "function(){return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0];}"
        }
    },

    populate: function (items, options) {
        options || (options = {});
        var self = this, c = this.constants;
        this.config = {
            left_y_axis_title: options.left_y_axis_title || "",
            right_y_axis_title: options.right_y_axis_title || "",
            right_y_axis_second_title: options.right_y_axis_second_title || "",
            chart_line_type: options.chart_line_type || c.NORMAL,
            chart_color: options.chart_color || [],
            chart_style: options.chart_style || c.NORMAL,
            left_y_axis_style: options.left_y_axis_style || c.NORMAL,
            right_y_axis_style: options.right_y_axis_style || c.NORMAL,
            right_y_axis_second_style: options.right_y_axis_second_style || c.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            show_right_y_axis_title: options.show_right_y_axis_title || false,
            show_right_y_axis_second_title: options.show_right_y_axis_second_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
            right_y_axis_reversed: options.right_y_axis_reversed || false,
            right_y_axis_second_reversed: options.right_y_axis_second_reversed || false,
            left_y_axis_number_level: options.left_y_axis_number_level || c.NORMAL,
            right_y_axis_number_level:  options.right_y_axis_number_level || c.NORMAL,
            right_y_axis_second_number_level: options.right_y_axis_second_number_level || c.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            right_y_axis_unit: options.right_y_axis_unit || "",
            right_y_axis_second_unit: options.right_y_axis_second_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || c.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_data_table: options.show_data_table || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            show_zoom: options.show_zoom || false,
            text_direction: options.text_direction || 0,
            cordon: options.cordon || []
        };
        this.options.items = items;

        var types = [];
        BI.each(items, function(idx, axisItems){
            var type = [];
            BI.each(axisItems, function(id, item){
                type.push(BICst.WIDGET.AREA);
            });
            types.push(type);
        });

        this.yAxis = [];
        BI.each(types, function(idx, type){
            if(BI.isEmptyArray(type)){
                return;
            }
            var newYAxis = {
                type: "value",
                title: {
                    style: {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px","fontWeight":""}
                },
                labelStyle: {
                    "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
                },
                position: idx > 0 ? "right" : "left",
                lineWidth: 1,
                axisIndex: idx,
                gridLineWidth: 0
            };
            self.yAxis.push(newYAxis);
        });

        this.combineChart.populate(items, types);
    },

    resize: function () {
        this.combineChart.resize();
    },

    magnify: function(){
        this.combineChart.magnify();
    }
});
BI.AreaChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.area_chart', BI.AreaChart);