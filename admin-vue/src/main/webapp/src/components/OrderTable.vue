<template>
    <div class="table-container">
        <v-data-table :search="search" :headers="headers" :items="items.map(item => {
            item.creationDate = formatDate(item.creationDate)
            item.lastModification = formatDate(item.updateDate)
            return item
        })" @click:row="onRowClick">
            <template v-slot:top>
                <v-toolbar flat>
                    <v-text-field
                            v-model="search"
                            append-icon="mdi-magnify"
                            label="Search"
                            single-line
                            hide-details>
                    </v-text-field>
                </v-toolbar>
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