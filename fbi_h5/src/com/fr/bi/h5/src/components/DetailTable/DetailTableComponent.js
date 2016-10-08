import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {ReactComponentWithImmutableRenderMixin, requestAnimationFrame} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch
} from 'lib'

import {Widget} from 'data'

import {TableWidget} from 'widgets';

import DetailTableComponentHelper from './DetailTableComponentHelper';
import TableComponentWidthHelper from '../Table/TableComponentWidthHelper'
import TableCell from '../Table/TableCell'
import TableHeader from '../Table/TableHeader'

import {Table} from 'base'

const {ColumnGroup, Column, Cell} = Table;


class DetailTableComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this._tableHelper = new DetailTableComponentHelper(props.$$widget);
        this._widthHelper = new TableComponentWidthHelper(this._tableHelper, props.width);
    }

    state = {
        data: []
    };

    componentWillMount() {
        this._fetchData();
    }

    componentDidMount() {

    }

    _fetchData() {
        new Widget(this.props.$$widget).getData().then((data)=> {
            this._tableHelper.setData(data);
            this.forceUpdate();
        })
    }

    render() {
        const {width, height} = this.props;
        const items = this._tableHelper.getItems();
        this._widthHelper.setItems(items);
        return <TableWidget
            width={width}
            height={height}
            freezeCols={this._tableHelper.isFreeze() ? [0] : []}
            columnSize={this._widthHelper.getWidth()}
            header={this._tableHelper.getHeader()}
            items={items}
            headerCellRenderer={(colIndex, cell)=> {
                return <TableHeader {...cell}/>
            }}
            itemsCellRenderer={({colIndex, rowIndex, items, ...props}) => {
                return <TableCell {...items[colIndex][rowIndex]} {...props}/>
            }}
        >
        </TableWidget>
    }
}
mixin.onClass(DetailTableComponent, ReactComponentWithImmutableRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default DetailTableComponent
