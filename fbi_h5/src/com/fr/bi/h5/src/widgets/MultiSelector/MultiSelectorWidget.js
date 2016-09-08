import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
    } from 'lib'

import {VirtualScroll, AutoSizer} from 'base'

import Item from './Item'
import MultiSelectorWidgetHelper from './MultiSelectorWidgetHelper'


class MultiSelectorWidget extends Component {
    constructor(props, context) {
        super(props, context);
        this._helper = new MultiSelectorWidgetHelper(props);
        this.state = {
            value: props.value,
            type: props.type
        }
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
        this._helper = new MultiSelectorWidgetHelper(nextProps);
        this.setState({value: nextProps.value, type: nextProps.type});
    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props;
        return <VirtualScroll
            width={props.width}
            height={props.height}
            overscanRowCount={0}
            //noRowsRenderer={this._noRowsRenderer.bind(this)}
            rowCount={this.props.items.length}
            rowHeight={30}
            rowRenderer={this._rowRenderer.bind(this)}
            //scrollToIndex={scrollToIndex}
            />
    }

    _rowRenderer({ index, isScrolling }) {
        const rowData = this._helper.getSortedItems()[index];
        return <Item key={rowData.value} onSelected={(sel)=> {
            if (sel) {
                this._helper.selectOneValue(rowData.value);
            } else {
                this._helper.disSelectOneValue(rowData.value);
            }
            this.setState({
                value: this._helper.getSelectedValue()
            });
        }} {...rowData}/>;
    }
}
mixin.onClass(MultiSelectorWidget, PureRenderMixin);
const styles = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default MultiSelectorWidget
