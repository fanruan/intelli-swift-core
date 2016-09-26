export default class CellSizeCache {
  constructor () {
    this._cachedColumnWidths = {};
    this._cachedRowHeights = {};
  }

  clearAllColumnWidths () {
    this._cachedColumnWidths = {}
  }

  clearAllRowHeights () {
    this._cachedRowHeights = {}
  }

  clearColumnWidth (index) {
    delete this._cachedColumnWidths[index]
  }

  clearRowHeight (index) {
    delete this._cachedRowHeights[index]
  }

  getColumnWidth (index) {
    return this._cachedColumnWidths[index]
  }

  getRowHeight (index) {
    return this._cachedRowHeights[index]
  }

  hasColumnWidth (index) {
    return this._cachedColumnWidths[index] >= 0
  }

  hasRowHeight (index) {
    return this._cachedRowHeights[index] >= 0
  }

  setColumnWidth (index, width) {
    this._cachedColumnWidths[index] = width
  }

  setRowHeight (index, height) {
    this._cachedRowHeights[index] = height
  }
}
