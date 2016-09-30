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
import {Size} from 'data'

import Item from './Item'
import MultiTreeSelectorWidgetHelper from './MultiTreeSelectorWidgetHelper'
import MultiTreeSelectorWidgetAsyncHelper from './MultiTreeSelectorWidgetAsyncHelper'


class MultiTreeSelectorWidget extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        items: [],
        floors: 0
    };

    state = {
        value: this.props.value,
        items: this.props.items,
        hasNext: false
    };

    _getNextState(props, state) {
        const {items, value, hasNext} = {...props, ...state};
        return {
            items,
            value,
            hasNext
        }
    }

    componentWillMount() {

    }

    componentDidMount() {
        if (this.props.itemsCreator) {
            this.props.itemsCreator({
                floors: this.props.floors,
                type: 0,
                times: -1,
                selected_values: this.props.value
            }).then((data)=> {
                const {items, hasNext} = data;
                this.setState({
                    items,
                    hasNext
                })
            })
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState(this._getNextState({...this.props, ...this.state}, nextProps));
    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        if (props.itemsCreator) {
            this._helper = new MultiTreeSelectorWidgetAsyncHelper(state, props);
        } else {
            this._helper = new MultiTreeSelectorWidgetHelper(state);
        }
        return <VirtualScroll
            width={props.width}
            height={props.height}
            overscanRowCount={10}
            //noRowsRenderer={this._noRowsRenderer.bind(this)}
            rowCount={this._helper.getSortedItems().length}
            rowHeight={Size.ITEM_HEIGHT}
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
        this._helper[expanded ? 'expandOneNode' : 'collapseOneNode'](rowData).then(()=> {
            this.setState({
                items: this._helper.getItems()
            });
        });
    }

    _onSelected(rowData, sel) {
        if (sel.checked === true) {
            this._helper.selectOneNode(rowData);
        } else {
            this._helper.disSelectOneNode(rowData);
        }
        this.setState({
            items: this._helper.getItems()
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
