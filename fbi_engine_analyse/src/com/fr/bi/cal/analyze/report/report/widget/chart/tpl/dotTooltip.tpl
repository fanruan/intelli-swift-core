function() {
function dealContentItem(key1, value, format) {return "<br>" + key1 + ":" + BI.contentFormat(value, format + "");}
var result = this.options.description + " " + this.seriesName;
if(+this.x){result += dealContentItem('${key1X}', this['${key2X}'], '${formatX}')}
if(+this.y){result += dealContentItem('${key1Y}', this['${key2Y}'], '${formatY}')}
if(+this.size){result += dealContentItem('${key1SIZE}', this['${key2SIZE}'], '${formatSIZE}')}
return result;
}