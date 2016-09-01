import { Component, PropTypes } from 'react'
import mixin from 'react-mixin'
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'
import Grid from '../Grid'

class ColumnSizer extends Component {
  static propTypes = {
    children: PropTypes.func.isRequired,

    columnMaxWidth: PropTypes.number,

    columnMinWidth: PropTypes.number,

    columnCount: PropTypes.number.isRequired,

    width: PropTypes.number.isRequired
  };

  constructor (props, context) {
    super(props, context)

    this._registerChild = this._registerChild.bind(this)
  }

  componentDidUpdate (prevProps, prevState) {
    const {
      columnMaxWidth,
      columnMinWidth,
      columnCount,
      width
    } = this.props;

    if (
      columnMaxWidth !== prevProps.columnMaxWidth ||
      columnMinWidth !== prevProps.columnMinWidth ||
      columnCount !== prevProps.columnCount ||
      width !== prevProps.width
    ) {
      if (this._registeredChild) {
        this._registeredChild.recomputeGridSize()
      }
    }
  }

  render () {
    const {
      children,
      columnMaxWidth,
      columnMinWidth,
      columnCount,
      width
    } = this.props;

    const safeColumnMinWidth = columnMinWidth || 1;

    const safeColumnMaxWidth = columnMaxWidth
      ? Math.min(columnMaxWidth, width)
      : width;

    let columnWidth = width / columnCount;
    columnWidth = Math.max(safeColumnMinWidth, columnWidth);
    columnWidth = Math.min(safeColumnMaxWidth, columnWidth);
    columnWidth = Math.floor(columnWidth);

    let adjustedWidth = Math.min(width, columnWidth * columnCount);

    return children({
      adjustedWidth,
      getColumnWidth: () => columnWidth,
      registerChild: this._registerChild
    })
  }

  _registerChild (child) {
    this._registeredChild = child;

    if (this._registeredChild) {
      this._registeredChild.recomputeGridSize()
    }
  }
}
mixin.onClass(ColumnSizer, ReactComponentWithPureRenderMixin);

export default ColumnSizer
