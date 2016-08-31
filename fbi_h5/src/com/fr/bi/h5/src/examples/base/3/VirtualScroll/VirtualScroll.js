import React, { Component, PropTypes } from 'lib'
import { VirtualScroll } from 'base'

export default class VirtualScrollDemo extends Component {

    constructor(props) {
        super(props)

        this.state = {
            overscanRowCount: 0,
            rowCount: 1000,
            scrollToIndex: undefined,
            virtualScrollHeight: 300,
            virtualScrollRowHeight: 50
        }

        this._getRowHeight = this._getRowHeight.bind(this)
        this._noRowsRenderer = this._noRowsRenderer.bind(this)
        this._onRowCountChange = this._onRowCountChange.bind(this)
        this._onScrollToRowChange = this._onScrollToRowChange.bind(this)
        this._rowRenderer = this._rowRenderer.bind(this)
    }

    render() {
        const {
            overscanRowCount,
            rowCount,
            scrollToIndex,
            virtualScrollHeight,
            virtualScrollRowHeight
            } = this.state

        return (
            <VirtualScroll
                height={virtualScrollHeight}
                overscanRowCount={overscanRowCount}
                noRowsRenderer={this._noRowsRenderer}
                rowCount={rowCount}
                rowHeight={virtualScrollRowHeight}
                rowRenderer={this._rowRenderer}
                scrollToIndex={scrollToIndex}
                width={300}
            />
        )
    }


    _getRowHeight({ index }) {
        return 35
    }

    _noRowsRenderer() {
        return (
            <div>
                无内容
            </div>
        )
    }

    _onRowCountChange(event) {
        const rowCount = parseInt(event.target.value, 10) || 0

        this.setState({rowCount})
    }

    _onScrollToRowChange(event) {
        const { rowCount } = this.state
        let scrollToIndex = Math.min(rowCount - 1, parseInt(event.target.value, 10))

        if (isNaN(scrollToIndex)) {
            scrollToIndex = undefined
        }

        this.setState({scrollToIndex})
    }

    _rowRenderer({ index, isScrolling }) {

        if (isScrolling) {
            return (
                <div>
                    <span>
                        Scrolling...
                    </span>
                </div>
            )
        }

        return (
            <div>
                This is row {index}
            </div>
        )
    }
}

export default VirtualScrollDemo
