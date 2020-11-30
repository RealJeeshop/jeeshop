<template>
    <div class="table-container">
        <v-data-table :search="search" :headers="headers" :items="items.map(item => {
            item.login = item.user.login
            item.reference = 'itemref'
            item.creationDate = formatDate(item.creationDate)
            item.updateDate = formatDate(item.updateDate)
            return item
        })" @click:row="onRowClick">
            <template v-slot:top>
                <div class="table-search-header">
                    <v-checkbox label="Payment validation orders"
                                hint="Check to show only orders with payment validated"/>

                    <v-text-field
                            v-model="search"
                            append-icon="mdi-magnify"
                            label="Search by order id, transaction id, user login, first name or last name (todo)"
                            single-line
                            hide-details>
                    </v-text-field>
                </div>
            </template>
        </v-data-table>
    </div>

</template>

<script>

    import DateUtils from '../lib/dateUtils'

    export default {
        name: "OrderTable",
        props: {
            headers: Array[Object],
            items: Array[Object]
        },
        data: () => {
            return {
                search: ""
            }
        },
        methods: {
            onRowClick(item) {
                this.$emit('item-selected', item.id)
            },
            formatDate(date) {
                return DateUtils.formatDate(date)
            }
        },
    }
</script>

<style>
    tr {
        cursor: pointer;
    }
</style>

<style lang="scss" scoped>

    .table-search-header {
        display: flex;
        padding: 0 1em 0 1em;
    }
    .table-search-header div:first-child {
        flex: 2;
    }

    .table-search-header div:last-child {
        flex: 4;
        padding-left: 3em;
    }

    .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1em;

        div:nth-child(2) {
            display: flex;
            align-items: center;

            input {
                margin-right: 1em;
            }
        }
    }
</style>