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
        rowHeight: 30,
        headerRowHeight: 30,
        header: [],
        items: [],
        itemsCellRenderer: emptyFunction,
        headerCellRenderer: emptyFunction
    };

    state = {};

    render() {
        const {isNeedFreeze, freezeCols, rowHeight, headerRowHeight, header, items, itemsCellRenderer, headerCellRenderer, width, height} = this.props;
        const columns = [];
        header.forEach((row, index)=> {
            columns.push(<Column
                fixed={freezeCols.indexOf(index) > -1}
                header={headerCellRenderer(index)}
                cell={itemsCellRenderer(index)}
                width={300}
            ></Column>)
        });

        return <Table
            rowHeight={rowHeight}
            groupHeaderHeight={0}
            headerHeight={headerRowHeight}
            rowsCount={items[0] ? items[0].length : 0}
            width={width}
            height={height}>
            {columns}
        </Table>
    }

}
mixin.onClass(TableWidget, PureRenderMixin);
const style = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default TableWidget
