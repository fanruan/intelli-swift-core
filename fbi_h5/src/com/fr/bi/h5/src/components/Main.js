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

import {Grid} from 'base'
import {TableWidget} from 'widgets'

import ChartComponent from './charts/ChartComponent.js'
const {width, height} = Dimensions.get('window');

class Main extends Component {
    static propTypes = {}

    constructor(props, context) {
        super(props, context);
        console.log(props);
        var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.rows = BI.keys(props.template.popConfig.widgets);
        this.state = {
            dataSource: ds.cloneWithRows(this.rows),
        }
    }

    render() {
        // return <Grid
        //     cellRenderer = {this._cellRender.bind(this)}
        //     columnWidth = {width}
        //     columnCount = {1}
        //     rowCount = {this.rows.length}
        //     height = {height}
        //     width = {width}
        //     overscanColumnCount = {0}
        //     overscanRowCount = {0}
        //     rowHeight = {height/2}
        //     scrollToColumn = {0}
        //     scrollToRow = {0}
        //     >
        //
        // </Grid>
        // return <ListView
        //    initialListSize={3}
        //    dataSource={this.state.dataSource}
        //    renderRow={this._renderRow.bind(this)}
        //    />
        return <View
            style={{position: 'absolute', left: 0, top: 0, right: 0, bottom: 0}}
        >
            <TableWidget
                isNeedFreeze={true}
                rowCount={100}
                freezeCols={true}
                headerRowCount={1}

                regions={{
                    topLeft: {
                        columnCount: 5,
                        columnWidth: 50,
                        cellRenderer: ({columnIndex, rowIndex})=> {
                            return <Text>{`${columnIndex}-${rowIndex}`}</Text>
                        }
                    },
                    topRight: {
                        columnCount: 10,
                        columnWidth: 50,
                        cellRenderer: ({columnIndex, rowIndex})=> {
                            return <Text>{`${columnIndex}-${rowIndex}`}</Text>
                        }
                    },
                    bottomLeft: {
                        columnCount: 5,
                        columnWidth: 50,
                        cellRenderer: ({columnIndex, rowIndex})=> {
                            return <Text>{`${columnIndex}-${rowIndex}`}</Text>
                        }
                    },
                    bottomRight: {
                        columnCount: 10,
                        columnWidth: 50,
                        cellRenderer: ({columnIndex, rowIndex})=> {
                            return <Text>{`${columnIndex}-${rowIndex}`}</Text>
                        }
                    }
                }}
            >
            </TableWidget>
        </View>
    }

    _cellRender({columnIndex, rowIndex}) {
        return <ChartComponent key={this.rows[rowIndex]} template={this.props.template} id={this.rows[rowIndex]}
                               height={height / 2}></ChartComponent>
    }

    _renderRow(rowData, sectionID, rowID) {
        return <ChartComponent key={rowData} template={this.props.template} id={rowData}
                               height={height / 2}></ChartComponent>
    }
}
mixin.onClass(Main, PureRenderMixin);
export default Main
