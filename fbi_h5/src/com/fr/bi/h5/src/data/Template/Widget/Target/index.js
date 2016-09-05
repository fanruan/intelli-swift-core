class Target {
    constructor(target, id) {
        this.target = target;
    }

    getName() {
        return this.target.name;
    }

    isUsed() {
        return this.target.used;
    }
}
export default Target