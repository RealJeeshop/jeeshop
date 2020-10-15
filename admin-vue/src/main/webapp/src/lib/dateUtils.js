export default {
    formatDate(date) {
        return date > 1
            ? new Date(date).toLocaleDateString('en-gb')
            : date
    }
}