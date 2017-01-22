import React, { Component, PropTypes } from 'lib'
import { AutoSizer, InfiniteLoader, VirtualScroll } from 'base'

const STATUS_LOADING = 1
const STATUS_LOADED = 2

export default class InfiniteLoaderDemo extends Component {
    constructor (props) {
        super(props)

        this.state = {
            loadedRowCount: 0,
            loadedRowsMap: {},
            loadingRowCount: 0,
            randomScrollToIndex: null
        }

        this._timeoutIdMap = {}

        this._clearData = this._clearData.bind(this)
        this._isRowLoaded = this._isRowLoaded.bind(this)
        this._loadMoreRows = this._loadMoreRows.bind(this)
        this._rowRenderer = this._rowRenderer.bind(this)
    }

    componentWillUnmount () {
        Object.keys(this._timeoutIdMap).forEach(timeoutId => {
            clearTimeout(timeoutId)
        })
    }

    render () {
        const { list, ...props } = this.props
        const { loadedRowCount, loadingRowCount, randomScrollToIndex } = this.state

        return (

            <div>
                <button onClick = {this._clearData}>Clear</button>
                <div>
                    {`${loadingRowCount} loading, ${loadedRowCount} loaded`}
                </div>
                <InfiniteLoader
                    isRowLoaded={this._isRowLoaded}
                    loadMoreRows={this._loadMoreRows}
                    rowCount={1000}
                >
                    {({ onRowsRendered, registerChild }) => (
                        <AutoSizer disableHeight>
                            {({ width }) => (
                                <VirtualScroll
                                    ref={registerChild}
                                    height={200}
                                    onRowsRendered={onRowsRendered}
                                    rowCount={1000}
                                    rowHeight={30}
                                    rowRenderer={this._rowRenderer}
                                    scrollToIndex={randomScrollToIndex}
                                    width={width}
                                />
                            )}
                        </AutoSizer>
                    )}
                </InfiniteLoader>
                </div>
        )
    }

    _clearData () {
        this.setState({
            loadedRowCount: 0,
            loadedRowsMap: {},
            loadingRowCount: 0
        })
    }

    _isRowLoaded ({ index }) {
        const { loadedRowsMap } = this.state
        return !!loadedRowsMap[index] // STATUS_LOADING or STATUS_LOADED
    }

    _loadMoreRows ({ startIndex, stopIndex }) {
        const { loadedRowsMap, loadingRowCount } = this.state
        const increment = stopIndex - startIndex + 1

        for (var i = startIndex; i <= stopIndex; i++) {
            loadedRowsMap[i] = STATUS_LOADING
        }

        this.setState({
            loadingRowCount: loadingRowCount + increment
        })

        const timeoutId = setTimeout(() => {
            const { loadedRowCount, loadingRowCount } = this.state

            delete this._timeoutIdMap[timeoutId]

            for (var i = startIndex; i <= stopIndex; i++) {
                loadedRowsMap[i] = STATUS_LOADED
            }

            this.setState({
                loadingRowCount: loadingRowCount - increment,
                loadedRowCount: loadedRowCount + increment
            })

            promiseResolver()
        }, 1000 + Math.round(Math.random() * 2000))

        this._timeoutIdMap[timeoutId] = true

        let promiseResolver

        return new Promise(resolve => {
            promiseResolver = resolve
        })
    }

    _rowRenderer ({ index }) {
        const { loadedRowsMap } = this.state

        let content

        if (loadedRowsMap[index] === STATUS_LOADED) {
            content = index
        } else {
            content = (
                <div/>
            )
        }

        return (
            <div
                key={index}
                style={{ height: 30 }}
            >
                {content}
            </div>
        )
    }
}
export default InfiniteLoaderDemo
