class Dimension {
    constructor(dimension, id) {
        this.dimension = dimension;
    }

    getName() {
        return this.dimension.name;
    }

    isUsed() {
        return this.dimension.used;
    }
}
export default Dimension