/**
 * WidgetFactory
 * Created by Young's on 2016/10/12.
 */
import TableWidget from './TableWidget'
import ChartWidget from './ChartWidget'
import ContentWidget from './ContentWidget'
import DetailWidget from './DetailWidget'
import ImageWidget from './ImageWidget'
import StringControl from './StringControl'
import TreeControl from './TreeControl'
import DateControl from './DateControl'
import WebWidget from './WebWidget'


export default {
    createWidget: ($widget, ...props)=> {
        const wType = $widget.get('type');
        switch (wType) {
            case BICst.WIDGET.TABLE:
                return new TableWidget($widget, ...props);
                break;
            //case BICst.WIDGET.CROSS_TABLE:
            //case BICst.WIDGET.COMPLEX_TABLE:
            //
            case BICst.WIDGET.DETAIL:
                return new DetailWidget($widget, ...props);
                break;

            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PIE :
            case BICst.WIDGET.DONUT :
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.FUNNEL:
                return new ChartWidget($widget, ...props);
                break;
            case BICst.WIDGET.NUMBER:
            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD:
                return new DateControl($widget, ...props);
            case BICst.WIDGET.QUERY:
            case BICst.WIDGET.RESET:
                break;
            case BICst.WIDGET.CONTENT:
                return new ContentWidget($widget, ...props);
                break;
            case BICst.WIDGET.IMAGE:
                return new ImageWidget($widget, ...props);
                break;
            case BICst.WIDGET.WEB:
                return new WebWidget($widget, ...props);
                break;
            case BICst.WIDGET.STRING:
                return new StringControl($widget, ...props);
                break;
            case BICst.WIDGET.TREE:
                return new TreeControl($widget, ...props);
                break;
            default:
                break;
        }
    }

};