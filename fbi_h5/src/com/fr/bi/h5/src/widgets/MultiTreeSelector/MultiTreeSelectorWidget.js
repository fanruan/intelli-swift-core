import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
    } from 'lib'

import {Table, AutoSizer} from 'base'

import Item from './Item'
import MultiTreeSelectorWidgetHelper from './MultiTreeSelectorWidgetHelper'


class MultiTreeSelectorWidget extends Component {
    constructor(props, context) {
        super(props, context);
        this.ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1.value !== r2.value});
        this._helper = new MultiTreeSelectorWidgetHelper(props);
        this.state = {
            dataSource: this.ds.cloneWithRows(this._helper.getItems())
        };
    }

    static propTypes = {};

    static defaultProps = {
        items: []
    };

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {
        this._helper = new MultiTreeSelectorWidgetHelper(nextProps);
        this.setState({dataSource: this.ds.cloneWithRows(this._helper.getItems())});
    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props;
        return <ListView
            {...props}
            dataSource={this.state.dataSource}
            initialListSize={100}
            renderRow={this._renderRow.bind(this)}
            >
        </ListView>
    }

    _renderRow(rowData, sectionID, rowID) {
        return <Item key={rowData.value} onSelected={(sel)=> {
            if (sel) {
                this._helper.selectOneValue(rowData.value);
            } else {
                this._helper.disSelectOneValue(rowData.value);
            }
            this.setState({
                dataSource: this.ds.cloneWithRows(this._helper.getItems())
            });
        }} {...rowData}/>;
    }
}
mixin.onClass(MultiTreeSelectorWidget, PureRenderMixin);
const styles = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default MultiTreeSelectorWidget
