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

import {Grid} from 'base'
import {TableWidget} from 'widgets'

import ChartComponent from './charts/ChartComponent.js'
import TableComponent from './tables/TableComponent.js'
const {width, height} = Dimensions.get('window');

class Main extends Component {
    static propTypes = {}

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
            initialListSize={3}
            dataSource={this.state.dataSource}
            renderRow={this._renderRow.bind(this)}
            />
    }

    _renderRow(rowData, sectionID, rowID) {
        const {template} = this.props;
        const widgetObj = template.getWidgetById(rowData);
        const type = widgetObj.getType();
        switch (type) {
            case BICst.WIDGET.TABLE:
                return <TableComponent key={rowData} widget={widgetObj}
                                       width={width} height={height / 2}></TableComponent>
            default:
                return <ChartComponent key={rowData} widget={widgetObj}
                                       width={width} height={height / 2}></ChartComponent>
        }
    }
}
mixin.onClass(Main, PureRenderMixin);
export default Main
