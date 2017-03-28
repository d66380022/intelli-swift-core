BI.DynamicTab = FR.extend(BI.MVCWidget, {

    _defaultConfig: function () {
        return BI.extend(BI.DynamicTab.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-sheet-tab-dynamic",
            model : {
                items: []
            },
            height: 30
        })
    },

    _initController : function (){
        return BI.DynamictabController;
    },

    _initModel : function (){
        return BI.DynamictabModel;
    },

    _initView: function () {
        var o = this.options;
        this.tabButton = BI.createWidget( {
            type: "bi.dynamic_tab_button",
            items :[]
        })
        var self = this;
        this.tabButton.on(BI.DynamicTabButton.ADD_SHEET, function(v){
            self.controller.addNewSheet({});
        })

        this.tabButton.on(BI.DynamicTabButton.MERGE_SHEET, function(v){
            //TODO 这里要选择不同的sheet做不同的事儿
            self.controller.chooseSheetForMerge();
        })

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            cls: "tab-dynamic-center",
            tab: this.tabButton.tab,
            defaultShowIndex:false,
            cardCreator: BI.bind(this._createTabs, this)
        });
        BI.createWidget({
            type:"bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.center",
                    items: [this.tab],
                    hgap: 20
                }
            }, {
                el:this.tabButton,
                height: o.height
            }]
        })
    },

    _createTabs : function(v) {
        var tab = BI.createWidget({
            type: "bi.history_tab",
            cls : "bi-animate-right-in",
            allHistory: this.controller.hasMergeHistory(v)
        });
        var self = this;
        BI.nextTick(function () {
            self.registerChildWidget(v, tab,  {
                currentTables:function () {
                    return self.controller.getCurrentTables()
                },
                getSheetName : function () {
                    return self.controller.getSheetName(v)
                },
                setSheetName : function (name) {
                    self.controller.renameSheet(name, v)
                }
            })
        });
        var self = this;
        tab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function (data, oldSheets) {
            self.controller.changeMergeSheet(data, oldSheets, v)
        });

        tab.on(BI.HistoryTab.VALID_CHANGE, function () {
            self.controller.setTabValid(v)
        })

        tab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE, function () {
            self.controller.deleteMergeSheet(v)
        });
        return tab;
    }


})
BI.shortcut("bi.dynamic_tab", BI.DynamicTab);