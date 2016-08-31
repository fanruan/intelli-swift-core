var React = require('lib');
var FakeObjectDataListStore = require('../../helpers/FakeObjectDataListStore');
var {
    Component,
    StyleSheet,
    PanResponder,
    Text,
    View
} = React;
var {Wheeler, ScrollableWheeler, Grid} = require('base');

class WheelerDemo extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        var dataList = new FakeObjectDataListStore(10000)
        return (
            <ScrollableWheeler
                style = {{
                border: '1px solid #d3d3d3',
                // borderColor: '#d3d3d3',
    // borderStyle: solid;
    // borderWidth: 1px;
                }}
                onScrollStart={()=>console.log('onScrollStart')}
                onScrollEnd={()=>console.log('onScrollEnd')}
                rowHeight={50}
                headerHeight={50}
                footerHeight={50}
                scrollContentWidth={1200}
                rowsCount={dataList.getSize()}
                width={1000}
                height={500}
            >{({scrollX, scrollY, firstRowIndex, firstRowOffset, bodyHeight, reservedHeight})=> {
                console.log({scrollX, scrollY});
                return (
                    <Text style={{
                    marginTop: 50,
                    width: 1000,
                    height: bodyHeight
                    }}>{scrollX + "," + scrollY}</Text>
                );
                // return (<Grid
                //     cellRenderer={this._cellRender.bind(this)}
                //     columnWidth={this._getColumnWidth.bind(this)}
                //     columnCount={20}
                //     rowCount={dataList.getSize()}
                //     height={bodyHeight}
                //     width={1000}
                //     overscanColumnCount={0}
                //     overscanRowCount={0}
                //     rowHeight={50}
                //     scrollLeft={scrollX}
                //     scrollTop={scrollY}
                //     style={{
                //         marginTop: 50,
                //         overflow: 'hidden'
                //     }}
                // >
                // </Grid>)
            }}</ScrollableWheeler>
        )
    }

    _getColumnWidth({index}) {
        return 60;
    }

    _cellRender({columnIndex, rowIndex}) {
        return <Text>{'c:' + columnIndex + 'r:' + rowIndex}</Text>
    }
}

export default WheelerDemo
