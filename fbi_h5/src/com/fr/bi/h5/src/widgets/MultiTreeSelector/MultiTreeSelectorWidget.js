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

import {VirtualScroll, AutoSizer, TextButton} from 'base'
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
        hasNext: false,
        times: 0
    };

    _getNextState(props, state) {
        const {items, value, hasNext, times} = {...props, ...state};
        return {
            items,
            value,
            hasNext,
            times
        }
    }

    componentWillMount() {

    }

    _fetchData() {
        if (this.props.itemsCreator) {
            this.props.itemsCreator({
                floors: this.props.floors,
                selected_values: this.state.value,
                times: this.state.times + 1
            }).then((data)=> {
                this.setState(this._getNextState(this.props, {
                    ...this.state, ...{
                    times: this.state.times + 1,
                    hasNext: data.hasNext,
                    items: this.state.items.concat(data.items)
                }
            })
        )
            ;
        }
    )
    }
}

componentDidMount()
{
    this._fetchData();
}

componentWillReceiveProps(nextProps)
{
    this.setState(this._getNextState({...this.props, ...this.state
}
,
nextProps
))
;
}

componentWillUpdate()
{

}

render()
{
    const {...props} = this.props, {...state} = this.state;
    if (props.itemsCreator) {
        this._helper = new MultiTreeSelectorWidgetAsyncHelper(state, props);
    } else {
        this._helper = new MultiTreeSelectorWidgetHelper(state);
    }
    return <VirtualScroll
        width={props.width}
        height={props.height}
        overscanRowCount={0}
        //noRowsRenderer={this._noRowsRenderer.bind(this)}
        rowCount={this._helper.getSortedItems().length + 1}
        rowHeight={Size.ITEM_HEIGHT}
        rowRenderer={this._rowRenderer.bind(this)}
        //scrollToIndex={scrollToIndex}
        />
}

_moreRenderer()
{
    if (this.state.hasNext === true) {
        return <TextButton style={{height: Size.ITEM_HEIGHT}} text={'点击加载更多数据'} onPress={()=> {
                    this._fetchData();
                }}/>
    } else {
        return <TextButton style={{height: Size.ITEM_HEIGHT}} disabled={true} text={'无更多数据'}/>
    }
}

_rowRenderer({index, isScrolling})
{
    if (index === this._helper.getSortedItems().length) {
        return this._moreRenderer()
    } else {
        const rowData = this._helper.getSortedItems()[index];
        return <Item key={rowData.value} onSelected={(sel)=> {
                this._onSelected(rowData, sel);
            }} onExpand={(expanded)=> {
                this._onExpand(rowData, expanded);
            }} {...rowData}/>;
    }
}

_onExpand(rowData, expanded)
{
    this._helper[expanded ? 'expandOneNode' : 'collapseOneNode'](rowData).then(()=> {
        this.setState({
            items: this._helper.getItems()
        });
    });
}

_onSelected(rowData, sel)
{
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
