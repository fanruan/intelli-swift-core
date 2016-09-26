import React, {
    Component,
    Text,
    View
} from 'lib';
import {
    Grid,
    CellMeasurer
} from 'base';

class CellMeasurerDemo extends Component {

    render() {
        return (
            <CellMeasurer
                cellRenderer={this._cellRender.bind(this)}
                columnCount={100000}
                height={300}
                rowCount={20}
            >
                {
                    ({getColumnWidth}) => (
                        <Grid
                            cellRenderer={this._cellRender.bind(this)}
                            columnWidth={getColumnWidth}
                            columnCount={100000}
                            rowCount={20}
                            height={300}
                            width={350}
                            noContentRender={this._noContentRender.bind(this)}
                            overscanColumnCount={0}
                            overscanRowCount={0}
                            rowHeight={this._getRowHeight.bind(this)}
                            scrollToColumn={0}
                            scrollToRow={0}
                        >
                        </Grid>
                    )
                }

            </CellMeasurer>

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

export default CellMeasurerDemo
