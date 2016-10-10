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
import GroupHeader from './GroupHeader'
import GroupItems from './GroupItems'

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
        groupHeader: [],
        groupItems: [],
        itemsCellRenderer: emptyFunction,
        headerCellRenderer: emptyFunction,
        groupHeaderCellRenderer: emptyFunction,
        groupItemsCellRenderer: emptyFunction
    };

    state = {};

    render() {
        const {
            isNeedFreeze, freezeCols, columnSize, rowHeight, headerRowHeight,
            header, items, groupHeader, groupItems,
            itemsCellRenderer, headerCellRenderer, groupHeaderCellRenderer, groupItemsCellRenderer,
            width, height, ...others
        } = this.props;
        const fixColumns = [], columns = [];
        header.forEach((row, colIndex)=> {
            const column = <Column
                key={colIndex}
                fixed={isNeedFreeze && freezeCols.indexOf(colIndex) > -1}
                header={headerCellRenderer({colIndex, ...row})}
                cell={({rowIndex, ...props})=>(itemsCellRenderer({
                    colIndex,
                    rowIndex, ...items[colIndex][rowIndex], ...props
                }))}
                width={columnSize[colIndex]}
            ></Column>;
            if (isNeedFreeze && freezeCols.indexOf(colIndex) > -1) {
                fixColumns.push(column)
            } else {
                columns.push(column);
            }
        });

        return <Table
            rowHeight={rowHeight}
            groupHeaderHeight={headerRowHeight * groupHeader.length}
            headerHeight={headerRowHeight}
            rowsCount={items[0] ? items[0].length : 0}
            width={width}
            height={height}
            {...others}
        >
            {fixColumns.length > 0 ? (<ColumnGroup
                fixed={true}
                header={<GroupHeader headerRowHeight={headerRowHeight} groupHeader={groupHeader}
                                     groupHeaderCellRenderer={groupHeaderCellRenderer}/>}>
                {fixColumns}
            </ColumnGroup>) : null}
            <ColumnGroup
                header={<GroupItems columnSize={columnSize.slice(fixColumns.length)} headerRowHeight={headerRowHeight}
                                    groupHeader={groupHeader} groupItems={groupItems}
                                    groupItemsCellRenderer={groupItemsCellRenderer}/>}>
                {columns}
            </ColumnGroup>
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
