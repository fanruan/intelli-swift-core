import React, { Component } from 'lib'
import { ArrowKeyStepper, AutoSizer, Grid } from 'base'

export default class ArrowKeyStepperDemo extends Component {
    constructor(props) {
        super(props)

        this._getColumnWidth = this._getColumnWidth.bind(this)
        this._getRowHeight = this._getRowHeight.bind(this)
        this._cellRenderer = this._cellRenderer.bind(this)
    }

    render() {
        return (

            <ArrowKeyStepper
                columnCount={100}
                rowCount={100}
            >
                {({ onSectionRendered, scrollToColumn, scrollToRow }) => (
                    <AutoSizer disableHeight>
                        {({ width }) => (
                            <Grid
                                columnWidth={this._getColumnWidth}
                                columnCount={100}
                                height={200}
                                onSectionRendered={onSectionRendered}
                                cellRenderer={({ columnIndex, rowIndex }) => this._cellRenderer({ columnIndex, rowIndex, scrollToColumn, scrollToRow })}
                                rowHeight={this._getRowHeight}
                                rowCount={100}
                                scrollToColumn={scrollToColumn}
                                scrollToRow={scrollToRow}
                                width={width}
                            />
                        )}
                    </AutoSizer>
                )}
            </ArrowKeyStepper>
        )
    }

    _getColumnWidth({ index }) {
        return (1 + (index % 3)) * 60
    }

    _getRowHeight({ index }) {
        return (1 + (index % 3)) * 30
    }

    _cellRenderer({ columnIndex, rowIndex, scrollToColumn, scrollToRow }) {

        const color = columnIndex === scrollToColumn && rowIndex === scrollToRow ? 'red' : ''

        return (
            <div style={{backgroundColor: color}}>
                {`r:${rowIndex}, c:${columnIndex}`}
            </div>
        )
    }
}
export default ArrowKeyStepperDemo
