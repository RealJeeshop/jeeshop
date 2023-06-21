export default {
    formatDate(date) {
        return date > 1
            ? new Date(date).toISOString().split('T')[0]
            : date
    }
}