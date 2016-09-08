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

import {Grid, ScrollSync, AutoSizer} from 'base'


class TableWidget extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        isNeedFreeze: true,
        freezeCols: false,
        rowHeight: 25,
        headerRowHeight: 25,
        rowCount: 0,
        headerRowCount: 0,

        regions: {
            topLeft: {
                columnCount: 0,
                columnWidth: 0,
                cellRenderer: emptyFunction,
            },
            topRight: {},
            bottomLeft: {},
            bottomRight: {}
        }
    };

    state = {};

    _renderFreezeCols(width, height, onScrollVertical, scrollTop) {
        const {isNeedFreeze, freezeCols, rowHeight, headerRowHeight, rowCount, headerRowCount, regions} = this.props;
        return <ScrollSync>
            {({scrollLeft, onScroll})=> {
                return <View
                    style={{width: width, top: 0, left: 0, ...style.region}}
                >
                    <Grid
                        style={{left: 0, top: 0, ...style.region}}
                        cellRenderer={regions.topLeft.cellRenderer}
                        rowHeight={headerRowHeight}
                        columnWidth={regions.topLeft.columnWidth}
                        columnCount={regions.topLeft.columnCount}
                        rowCount={headerRowCount}
                        height={headerRowCount * headerRowHeight}
                        width={width}
                        overscanColumnCount={0}
                        overscanRowCount={0}
                        onScroll={({scrollLeft})=> {
                            onScroll({scrollLeft});
                        }}
                        scrollLeft={scrollLeft}
                    ></Grid>
                    <Grid
                        style={{left: 0, top: headerRowCount * headerRowHeight, ...style.region}}
                        cellRenderer={regions.bottomLeft.cellRenderer}
                        rowHeight={rowHeight}
                        columnWidth={regions.bottomLeft.columnWidth}
                        columnCount={regions.bottomLeft.columnCount}
                        rowCount={rowCount}
                        height={height - headerRowCount * headerRowHeight}
                        width={width}
                        overscanColumnCount={0}
                        overscanRowCount={0}
                        onScroll={(scl)=> {
                            onScroll({
                                scrollLeft: scl.scrollLeft
                            });
                            onScrollVertical({
                                scrollTop: scl.scrollTop
                            })
                        }}
                        scrollLeft={scrollLeft}
                        scrollTop={scrollTop || 0}
                    ></Grid>
                </View>

            }}
        </ScrollSync>
    }

    _renderNormalCols(width, height, onScrollVertical, scrollTop, regionWidth) {
        const {isNeedFreeze, freezeCols, rowHeight, headerRowHeight, rowCount, headerRowCount, regions} = this.props;
        return <ScrollSync>
            {({scrollLeft, onScroll})=> {
                const rs = []
                if (isNeedFreeze === true) {
                    rs.push(<Grid
                        style={{left: 0, top: 0, ...style.region}}
                        cellRenderer={regions.topRight.cellRenderer}
                        rowHeight={headerRowHeight}
                        columnWidth={regions.topRight.columnWidth}
                        columnCount={regions.topRight.columnCount}
                        rowCount={headerRowCount}
                        height={headerRowCount * headerRowHeight}
                        width={width}
                        overscanColumnCount={0}
                        overscanRowCount={0}
                        onScroll={({scrollLeft})=> {
                            onScroll({scrollLeft});
                        }}
                        scrollLeft={scrollLeft}
                    ></Grid>)
                }
                rs.push(<Grid
                    style={{left: 0, top: headerRowCount * headerRowHeight, ...style.region}}
                    cellRenderer={regions.bottomRight.cellRenderer}
                    rowHeight={rowHeight}
                    columnWidth={regions.bottomRight.columnWidth}
                    columnCount={regions.bottomRight.columnCount}
                    rowCount={rowCount}
                    height={height - headerRowCount * headerRowHeight}
                    width={width}
                    overscanColumnCount={0}
                    overscanRowCount={0}
                    onScroll={(scl)=> {
                        onScroll({
                            scrollLeft: scl.scrollLeft
                        });
                        onScrollVertical && onScrollVertical({
                            scrollTop: scl.scrollTop
                        })
                    }}
                    scrollLeft={scrollLeft}
                    scrollTop={scrollTop || 0}
                ></Grid>);
                return <View
                    style={{width: width, height: height, top: 0, left: regionWidth || 0, ...style.region}}
                >
                    {rs}
                </View>

            }}
        </ScrollSync>
    }

    render() {
        const {isNeedFreeze, freezeCols} = this.props;
        return <AutoSizer>
            {({width, height})=> {
                if (isNeedFreeze === true && freezeCols === true) {
                    return <ScrollSync>
                        {({scrollTop, onScroll})=> {
                            return <View
                                style={{width: width, height: height}}
                            >
                                {this._renderFreezeCols(width / 3, height, onScroll, scrollTop)}
                                {this._renderNormalCols(width - width / 3, height, onScroll, scrollTop, width / 3)}
                            </View>
                        }}
                    </ScrollSync>
                } else {
                    return this._renderNormalCols(width, height, emptyFunction, 0, 0);
                }
            }}
        </AutoSizer>
    }

}
mixin.onClass(TableWidget, PureRenderMixin);
const style = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default TableWidget
