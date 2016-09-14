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
import MultiTreeSelectorWidgetHelper from './MultiTreeSelectorWidgetHelper'
import MultiTreeSelectorWidgetAsyncHelper from './MultiTreeSelectorWidgetAsyncHelper'


class MultiTreeSelectorWidget extends Component {
    constructor(props, context) {
        super(props, context);
        if (props.itemsCreator) {
            this._helper = new MultiTreeSelectorWidgetAsyncHelper(props);
        } else {
            this._helper = new MultiTreeSelectorWidgetHelper(props);
        }
        this.state = {
            value: props.value
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
        if (nextProps.itemsCreator) {
            this._helper = new MultiTreeSelectorWidgetAsyncHelper(nextProps);
        } else {
            this._helper = new MultiTreeSelectorWidgetHelper(nextProps);
        }
        this.setState({value: nextProps.value});
    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props;
        return <VirtualScroll
            width={props.width}
            height={props.height}
            overscanRowCount={10}
            //noRowsRenderer={this._noRowsRenderer.bind(this)}
            rowCount={this._helper.getSortedItems().length}
            rowHeight={35}
            rowRenderer={this._rowRenderer.bind(this)}
            //scrollToIndex={scrollToIndex}
            />
    }

    _rowRenderer({index, isScrolling}) {
        const rowData = this._helper.getSortedItems()[index];
        return <Item key={rowData.value} onSelected={(sel)=> {
            this._onSelected(rowData, sel);
        }} onExpand={(expanded)=> {
            this._onExpand(rowData, expanded);
        }} {...rowData}/>;
    }

    _onExpand(rowData, expanded) {
        if (expanded) {
            this._helper.expandOneValue(rowData.value);
        } else {
            this._helper.collapseOneValue(rowData.value);
        }
        this.forceUpdate();
        // this.setState({
        //     value: this._helper.getSelectedValue()
        // });
    }

    _onSelected(rowData, sel) {
        if (sel) {
            this._helper.selectOneValue(rowData.value);
        } else {
            this._helper.disSelectOneValue(rowData.value);
        }
        this.setState({
            value: this._helper.getSelectedValue()
        });
    }
}
mixin.onClass(MultiTreeSelectorWidget, PureRenderMixin);
const styles = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default MultiTreeSelectorWidget
