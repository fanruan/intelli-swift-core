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

import {VirtualScroll, AutoSizer, Infinite} from 'base'

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

    handleInfiniteLoad() {
        var that = this;
    }

    elementInfiniteLoad() {
        return <div className="infinite-list-item">
            Loading...
        </div>;
    }

    render() {
        const {...props} = this.props;
        //return <Infinite
        //    elementHeight={44}
        //    containerHeight={props.height}
        //    infiniteLoadBeginEdgeOffset={200}
        //    onInfiniteLoad={this.handleInfiniteLoad}
        //    loadingSpinnerDelegate={this.elementInfiniteLoad()}
        //    isInfiniteLoading={true}
        //    timeScrollStateLastsForAfterUserScrolls={1000}
        //    ></Infinite>;
        return <VirtualScroll
            width={props.width}
            height={props.height}
            overscanRowCount={0}
            //noRowsRenderer={this._noRowsRenderer.bind(this)}
            rowCount={this._helper.getSortedItems().length}
            rowHeight={44}
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
            this.forceUpdate();
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
