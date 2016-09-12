import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'

import ChartComponent from './Chart/ChartComponent.js'
import TableComponent from './Table/TableComponent.js'
import DetailTableComponent from './DetailTable/DetailTableComponent.js'
import MultiSelectorComponent from './MultiSelector/MultiSelectorComponent.js'
import MultiTreeSelectorComponent from './MultiTreeSelector/MultiTreeSelectorComponent.js'

const {width, height} = Dimensions.get('window');

class Main extends Component {
    static propTypes = {};

    constructor(props, context) {
        super(props, context);
        console.log(props);
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        const rows = props.template.getAllWidgetIds();
        this.state = {
            dataSource: ds.cloneWithRows(rows)
        }
    }

    render() {
        return <ListView
            initialListSize={2}
            dataSource={this.state.dataSource}
            renderRow={this._renderRow.bind(this)}
        />
    }

    _renderRow(rowData, sectionID, rowID) {
        const {template} = this.props;
        const widgetObj = template.getWidgetById(rowData);
        const type = widgetObj.getType();
        const props = {
            key: rowData,
            widget: widgetObj,
            width: width,
            height: height / 3 * 2
        };
        switch (type) {
            case BICst.WIDGET.TABLE:
                return <TableComponent {...props} />;
            //case BICst.WIDGET.CROSS_TABLE:
            //case BICst.WIDGET.COMPLEX_TABLE:
            //
            case BICst.WIDGET.DETAIL:
                return <DetailTableComponent {...props} />;

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
                //case BICst.WIDGET.STRING:
                //case BICst.WIDGET.NUMBER:
                //case BICst.WIDGET.DATE:
                //case BICst.WIDGET.YEAR:
                //case BICst.WIDGET.QUARTER:
                //case BICst.WIDGET.MONTH:
                //case BICst.WIDGET.YMD:
                //case BICst.WIDGET.QUERY:
                //case BICst.WIDGET.RESET:
                //case BICst.WIDGET.CONTENT:
                //case BICst.WIDGET.IMAGE:
                //case BICst.WIDGET.WEB:
                return <ChartComponent {...props} />;
            case BICst.WIDGET.STRING:
                return <MultiSelectorComponent {...props} />;
            case BICst.WIDGET.TREE:
                return <MultiTreeSelectorComponent {...props} />;
            default:
                return null;
        }
    }
}
mixin.onClass(Main, PureRenderMixin);
export default Main
