import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, map, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch
} from 'lib'

import {VirtualScroll, AutoSizer, Infinite, TextButton, VtapeLayout} from 'base'
import {Colors, Size} from 'data'

import Item from './Item'
import MultiSelectorWidgetHelper from './MultiSelectorWidgetHelper'


class MultiSelectorWidget extends Component {
    constructor(props, context) {
        super(props, context);
        this.state = this._getNextState(props);
    }

    static propTypes = {};

    static defaultProps = {
        type: 0,
        value: [],
        items: [],
        hasNext: false
    };

    state = {};

    _getNextState(props, state = {}) {
        const nextState = {...props, ...state};
        this._helper = new MultiSelectorWidgetHelper(nextState);
        return {
            value: nextState.value,
            type: nextState.type,
            items: nextState.items,
            hasNext: nextState.hasNext
        }
    }

    componentWillMount() {

    }

    componentDidMount() {
        if (this.props.itemsCreator) {
            this.props.itemsCreator().then((data)=> {
                this.setState(this._getNextState({
                    ...this.props, ...{
                        hasNext: data.hasNext,
                        items: map(data.value, val=> {
                            return {value: val}
                        })
                    }
                }));
            })
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState(this._getNextState({...this.props, ...this.state}, nextProps));
    }

    componentWillUpdate() {

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
        return <VtapeLayout style={styles.wrapper}>
            <VirtualScroll
                width={props.width}
                height={props.height - Size.ITEM_HEIGHT}
                overscanRowCount={0}
                //noRowsRenderer={this._noRowsRenderer.bind(this)}
                rowCount={this._helper.getSortedItems().length}
                rowHeight={Size.ITEM_HEIGHT}
                rowRenderer={this._rowRenderer.bind(this)}
                //scrollToIndex={scrollToIndex}
            />
            <TextButton height={Size.ITEM_HEIGHT} style={styles.toolbar} text='全选'
                        onPress={this._onSelectAll.bind(this)}/>
        </VtapeLayout>;
    }

    _onSelectAll() {
        this._helper.setType(2);
        this.setState({
            type: 2
        });
    }

    _rowRenderer({index, isScrolling}) {
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
    wrapper: {
        flex: 1
    },
    toolbar: {
        borderTopWidth: 1 / PixelRatio.get(),
        borderTopColor: Colors.BORDER
    }
});
export default MultiSelectorWidget
