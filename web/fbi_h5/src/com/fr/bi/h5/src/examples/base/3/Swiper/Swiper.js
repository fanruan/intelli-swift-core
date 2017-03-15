var React = require('lib');
var FakeObjectDataListStore = require('../../helpers/FakeObjectDataListStore');
var {
    Component,
    Text
} = React;
var {Mover, Grid} = require('base');

class SwiperDemo extends Component {
    constructor(props) {
        super(props);
        this.dataList = new FakeObjectDataListStore(10000)
    }

    render() {
        return (
            <Mover
                onSwipeStart={(x,y)=>console.log(`onScrollStart${x},${y}`)}
                onSwipeEnd={(x,y)=>console.log(`onScrollEnd${x},${y}`)}
            >{({scrollX, scrollY})=> {
                console.log({scrollX, scrollY});
                return (<Grid
                    cellRenderer={this._cellRender.bind(this)}
                    columnWidth={this._getColumnWidth.bind(this)}
                    columnCount={20}
                    rowCount={10000}
                    height={400}
                    width={300}
                    overscanColumnCount={0}
                    overscanRowCount={0}
                    rowHeight={50}
                    scrollLeft={scrollX}
                    scrollTop={scrollY}
                    style={{
                        overflow: 'hidden'
                    }}
                >
                </Grid>)
            }}</Mover>
        )
    }

    _getColumnWidth({index}) {
        return 60;
    }

    _cellRender({columnIndex, rowIndex}) {
        return <Text>{'c:' + columnIndex + 'r:' + rowIndex}</Text>
    }
}

export default SwiperDemo
