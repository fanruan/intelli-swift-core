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
        const wi = new Widget(this.props.$$widget).createJson();
        Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
            method: "POST",
            body: JSON.stringify({widget: wi, sessionID: BH.sessionID})
        }).then(function (response) {
            return response.json();
        }).then((data)=> {
            this._tableHelper.setData(data);
            this.forceUpdate();
        });
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
mixin.onClass(DetailTableComponent, PureRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default DetailTableComponent
