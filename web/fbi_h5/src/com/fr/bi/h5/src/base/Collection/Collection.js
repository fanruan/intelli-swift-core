import React, {Component, PropTypes} from 'react'
import mixin from 'react-mixin'
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'
import CollectionView from './CollectionView'
import calculateSizeAndPositionData from './utils/calculateSizeAndPositionData'
import getUpdatedOffsetForIndex from '../Utilties/getUpdatedOffsetForIndex'

class Collection extends Component {

    static propTypes = {
        'aria-label': PropTypes.string,

        cellCount: PropTypes.number.isRequired,

        cellGroupRenderer: PropTypes.func.isRequired,

        cellRenderer: PropTypes.func.isRequired,

        cellSizeAndPositionGetter: PropTypes.func.isRequired,

        sectionSize: PropTypes.number
    };

    static defaultProps = {
        'aria-label': 'grid',
        cellGroupRenderer: defaultCellGroupRenderer
    };

    constructor(props, context) {
        super(props, context)

        this._cellMetadata = []
        this._lastRenderedCellIndices = []
    }

    /** See Collection#recomputeCellSizesAndPositions */
    recomputeCellSizesAndPositions() {
        this._collectionView.recomputeCellSizesAndPositions()
    }

    render() {
        const {...props} = this.props;

        return (
            <CollectionView
                cellLayoutManager={this}
                ref={(ref) => {
          this._collectionView = ref
        }}
                {...props}
            />
        )
    }

    calculateSizeAndPositionData() {
        const {cellCount, cellSizeAndPositionGetter, sectionSize} = this.props;

        const data = calculateSizeAndPositionData({
            cellCount,
            cellSizeAndPositionGetter,
            sectionSize
        });

        this._cellMetadata = data.cellMetadata;
        this._sectionManager = data.sectionManager;
        this._height = data.height;
        this._width = data.width;
    }

    getLastRenderedIndices() {
        return this._lastRenderedCellIndices
    }

    getScrollPositionForCell({
        align,
        cellIndex,
        height,
        scrollLeft,
        scrollTop,
        width
    }) {
        const {cellCount} = this.props;

        if (
            cellIndex >= 0 &&
            cellIndex < cellCount
        ) {
            const cellMetadata = this._cellMetadata[cellIndex];

            scrollLeft = getUpdatedOffsetForIndex({
                align,
                cellOffset: cellMetadata.x,
                cellSize: cellMetadata.width,
                containerSize: width,
                currentOffset: scrollLeft,
                targetIndex: cellIndex
            })

            scrollTop = getUpdatedOffsetForIndex({
                align,
                cellOffset: cellMetadata.y,
                cellSize: cellMetadata.height,
                containerSize: height,
                currentOffset: scrollTop,
                targetIndex: cellIndex
            })
        }

        return {
            scrollLeft,
            scrollTop
        }
    }

    getTotalSize() {
        return {
            height: this._height,
            width: this._width
        }
    }

    cellRenderers({
        height,
        isScrolling,
        width,
        x,
        y
    }) {
        const {cellGroupRenderer, cellRenderer} = this.props;

        // Store for later calls to getLastRenderedIndices()
        this._lastRenderedCellIndices = this._sectionManager.getCellIndices({
            height,
            width,
            x,
            y
        });

        return cellGroupRenderer({
            cellRenderer,
            cellSizeAndPositionGetter: ({index}) => this._sectionManager.getCellMetadata({index}),
            indices: this._lastRenderedCellIndices,
            isScrolling
        })
    }
}

function defaultCellGroupRenderer({
    cellRenderer,
    cellSizeAndPositionGetter,
    indices,
    isScrolling
}) {
    return indices
        .map((index) => {
            const cellMetadata = cellSizeAndPositionGetter({index})
            const renderedCell = cellRenderer({
                index,
                isScrolling
            })

            if (renderedCell == null || renderedCell === false) {
                return null
            }

            return (
                <div
                    key={index}
                    style={{
            position: 'absolute',
            height: cellMetadata.height,
            left: cellMetadata.x,
            top: cellMetadata.y,
            width: cellMetadata.width
          }}
                >
                    {renderedCell}
                </div>
            )
        })
        .filter((renderedCell) => !!renderedCell)
}
mixin.onClass(Collection, ReactComponentWithPureRenderMixin);

export default Collection
