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

import {Template, Widget} from 'data'
import {TableWidget} from 'widgets';

import TableComponentHelper from './TableComponentHelper';
import TableComponentWidthHelper from './TableComponentWidthHelper'
import TableCell from './TableCell'
import TableHeader from './TableHeader'

import {Table} from 'base'


class TableComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this._tableHelper = new TableComponentHelper(props);
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
        const template = new Template(this.props.$template);
        const wId = this.props.wId;
        const widget = template.getWidgetById(wId);
        widget.getData().then((data)=> {
            console.log(data);
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
            groupHeader={this._tableHelper.getGroupHeader()}
            groupItems={this._tableHelper.getGroupItems()}
            groupHeaderCellRenderer={({colIndex, ...cell})=> {
                return <TableHeader {...cell}/>
            }}
            groupItemsCellRenderer={({...cell})=> {
                return <TableHeader {...cell}/>
            }}
            headerCellRenderer={({colIndex, ...cell})=> {
                return <TableHeader {...cell}/>
            }}
            itemsCellRenderer={({colIndex, rowIndex, ...cell}) => {
                return <TableCell {...cell}/>
            }}
        >
        </TableWidget>
    }
}
mixin.onClass(TableComponent, ReactComponentWithImmutableRenderMixin);

const style = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default TableComponent
