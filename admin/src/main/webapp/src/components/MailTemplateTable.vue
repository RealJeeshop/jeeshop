<template>
    <div class="table-container">
        <v-data-table :search="search" :headers="headers" :items="items.map(user => {
            user.creationDate = formatDate(user.creationDate)
            user.updateDate = formatDate(user.updateDate)
            return user
        })" @click:row="onRowClick">
            <template v-slot:top>
                <div class="table-search-header">
                    <v-text-field
                            v-model="search"
                            append-icon="mdi-magnify"
                            label="Enter email template name"
                            single-line
                            hide-details>
                    </v-text-field>
                </div>
            </template>
        </v-data-table>
    </div>

</template>

<script>
    import DateUtils from "../lib/dateUtils";

    export default {
        name: 'MailTemplateTable',
        props: {
            headers: Array[Object],
            items: Array[Object]
        },
        data() {
            return {
                search: ""
            }
        },
        methods: {
            onRowClick(user) {
                this.$emit('item-selected', user.id)
            },
            formatDate(date) {
                return DateUtils.formatDate(date)
            }
        },
    }
</script>

<style lang="scss" scoped>
    .table-search-header {
        display: flex;
        padding: 0 1em 0 1em;
    }
</style>