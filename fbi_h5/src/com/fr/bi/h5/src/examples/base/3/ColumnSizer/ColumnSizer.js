import React, {
    Component,
    Text,
    View
} from 'lib';
import {
    Grid,
    ColumnSizer
} from 'base';

class ColumnSizerDemo extends Component {

    render() {
        return (
            <ColumnSizer
                columnMaxWidth={200}
                columnMinWidth={100}
                columnCount={2}
                width={300}
            >
                {({adjustedWidth, getColumnWidth, registerChild})=> (
                    <Grid
                        ref={registerChild}
                        cellRenderer={this._cellRender.bind(this)}
                        columnWidth={getColumnWidth}
                        columnCount={2}
                        rowCount={1}
                        height={300}
                        width={adjustedWidth}
                        noContentRender={this._noContentRender.bind(this)}
                        overscanColumnCount={0}
                        overscanRowCount={0}
                        rowHeight={this._getRowHeight.bind(this)}
                        scrollToColumn={0}
                        scrollToRow={0}
                    >

                    </Grid>
                )}
            </ColumnSizer>
        )
    }

    _getRowHeight({index}) {
        return 30;
    }

    _noContentRender() {
        return <Text>没有内容</Text>
    }

    _getColumnWidth({index}) {
        return 70;
    }

    _cellRender({columnIndex, rowIndex}) {
        return <Text>{'c:' + columnIndex + 'r:' + rowIndex}</Text>
    }
}

export default ColumnSizerDemo
