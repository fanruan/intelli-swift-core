import React, { Component } from 'lib'
import { Collection } from 'base'


const CELL_WIDTH = 75;
const GUTTER_SIZE = 3;

export default class CollectionDemo extends Component {

    constructor(props, context) {
        super(props, context)

        this.state = {
            cellCount: 1000,
            columnCount: this._getColumnCount(1000),
            height: 300,
            horizontalOverscanSize: 0,
            scrollToCell: undefined,
            showScrollingPlaceholder: true,
            verticalOverscanSize: 0
        }

        this._columnYMap = []

        this._cellRenderer = this._cellRenderer.bind(this)
        this._cellSizeAndPositionGetter = this._cellSizeAndPositionGetter.bind(this)
        this._noContentRenderer = this._noContentRenderer.bind(this)
        this._onCellCountChange = this._onCellCountChange.bind(this)
        this._onHeightChange = this._onHeightChange.bind(this)
        this._onHorizontalOverscanSizeChange = this._onHorizontalOverscanSizeChange.bind(this)
        this._onScrollToCellChange = this._onScrollToCellChange.bind(this)
        this._onVerticalOverscanSizeChange = this._onVerticalOverscanSizeChange.bind(this)
    }

    render() {
        const { cellCount, height, horizontalOverscanSize, scrollToCell, showScrollingPlaceholder, verticalOverscanSize } = this.state

        return (
            <Collection
                cellCount={cellCount}
                cellRenderer={this._cellRenderer}
                cellSizeAndPositionGetter={this._cellSizeAndPositionGetter}
                height={height}
                horizontalOverscanSize={horizontalOverscanSize}
                noContentRenderer={this._noContentRenderer}
                scrollToCell={scrollToCell}
                verticalOverscanSize={verticalOverscanSize}
                width={300}
            />
        )
    }

    _cellRenderer({ index, isScrolling }) {

        return (
            <div
            >
                {isScrolling ? '...' : index}
            </div>
        )
    }

    _cellSizeAndPositionGetter({ index }) {
        const { columnCount } = this.state

        const columnPosition = index % (columnCount || 1)

        const height = 50
        const width = CELL_WIDTH
        const x = columnPosition * (GUTTER_SIZE + width)
        const y = this._columnYMap[columnPosition] || 0

        this._columnYMap[columnPosition] = y + height + GUTTER_SIZE

        return {
            height,
            width,
            x,
            y
        }
    }

    _getColumnCount(cellCount) {
        return Math.round(Math.sqrt(cellCount))
    }

    _onHorizontalOverscanSizeChange(event) {
        const horizontalOverscanSize = parseInt(event.target.value, 10) || 0

        this.setState({horizontalOverscanSize})
    }

    _noContentRenderer() {
        return (
            <div >
                没内容
            </div>
        )
    }

    _onCellCountChange(event) {
        const cellCount = parseInt(event.target.value, 10) || 0

        this._columnYMap = []

        this.setState({
            cellCount,
            columnCount: this._getColumnCount(cellCount)
        })
    }

    _onHeightChange(event) {
        const height = parseInt(event.target.value, 10) || 0

        this.setState({height})
    }

    _onScrollToCellChange(event) {
        const { cellCount } = this.state

        let scrollToCell = Math.min(cellCount - 1, parseInt(event.target.value, 10))

        if (isNaN(scrollToCell)) {
            scrollToCell = undefined
        }

        this.setState({scrollToCell})
    }

    _onVerticalOverscanSizeChange(event) {
        const verticalOverscanSize = parseInt(event.target.value, 10) || 0

        this.setState({verticalOverscanSize})
    }
}

export default CollectionDemo
