;
!(function(){
    BI.NumberInRangeFilterValue = function(range){
        this.range = {};
        range = range || {};
        this.range.min = range.min || 0;
        this.range.max = range.max || 0;
        this.range.closemin = range.closemin || true;
        this.range.closemax = range.closemax || true;
    };
    BI.NumberInRangeFilterValue.prototype = {
        constructor: BI.NumberInRangeFilterValue,

        isNumberInRange: function(value){
            if(value = null){
                return false;
            }
            return (this.range.closemin ? value >= this.range.min : value > this.range.min) &&
                (this.range.closemax ? value <= this.range.max : value < this.range.max);
        },

        isQualified: function(value){
            return this.isNumberInRange(value);
        }
    }
})();