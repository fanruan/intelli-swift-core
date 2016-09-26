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

import {Table} from 'base'

const {ColumnGroup, Column, Cell} = Table;

class TableWidget extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        isNeedFreeze: true,
        freezeCols: [],
        columnSize: [],
        rowHeight: 30,
        headerRowHeight: 30,
        header: [],
        items: [],
        itemsCellRenderer: emptyFunction,
        headerCellRenderer: emptyFunction
    };

    state = {};

    render() {
        const {isNeedFreeze, freezeCols, columnSize, rowHeight, headerRowHeight, header, items, itemsCellRenderer, headerCellRenderer, width, height, ...others} = this.props;
        const columns = [];
        header.forEach((row, colIndex)=> {
            columns.push(<Column
                key={colIndex}
                fixed={freezeCols.indexOf(colIndex) > -1}
                header={headerCellRenderer(colIndex, row)}
                cell={(props)=>(itemsCellRenderer({colIndex, items, ...props}))}
                width={columnSize[colIndex]}
                ></Column>)
        });

        return <Table
            rowHeight={rowHeight}
            groupHeaderHeight={0}
            headerHeight={headerRowHeight}
            rowsCount={items[0] ? items[0].length : 0}
            width={width}
            height={height}
            {...others}
            >
            {columns}
        </Table>;
    }

}
mixin.onClass(TableWidget, PureRenderMixin);
const style = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default TableWidget
