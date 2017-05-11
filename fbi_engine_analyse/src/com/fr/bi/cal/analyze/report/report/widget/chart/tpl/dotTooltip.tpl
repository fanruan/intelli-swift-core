function() {
function dealContentItem(key1, value, format) {return "<br>" + key1 + ":" + BI.contentFormat(value, format + "");}
var result = this.options.description + " " + this.seriesName;
if(+this.x){result += dealContentItem('${key1_X}', this['${key2_X}'], '${format_X}')}
if(+this.y){result += dealContentItem('${key1_Y}', this['${key2_Y}'], '${format_Y}')}
if(+this.size){result += dealContentItem('${key1_SIZE}', this['${key2_SIZE}'], '${format_SIZE}')}
return result;
}