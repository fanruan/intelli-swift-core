import React, {
    Component,
    Text,
    View
} from 'lib';
import {
    Grid
} from 'base';

class GridDemo extends Component {

    render() {
        return (
            <Grid
                cellRenderer = {this._cellRender.bind(this)}
                columnWidth = {this._getColumnWidth.bind(this)}
                columnCount = {1000}
                rowCount = {1000}
                height = {300}
                width = {350}
                noContentRender = {this._noContentRender.bind(this)}
                overscanColumnCount = {0}
                overscanRowCount = {0}
                rowHeight = {this._getRowHeight.bind(this)}
                scrollToColumn = {0}
                scrollToRow = {0}
            >

            </Grid>
        )
    }

    _getRowHeight({index}){
        return 30;
    }

    _noContentRender(){
        return <Text>没有内容</Text>
    }

    _getColumnWidth({index}) {
        return 70;
    }

    _cellRender({columnIndex, rowIndex}) {
        return <Text>{'c:' + columnIndex + 'r:' + rowIndex}</Text>
    }
}

export default GridDemo
