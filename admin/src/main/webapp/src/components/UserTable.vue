<template>
    <div class="table-container">
        <v-data-table :search="search" :headers="headers" :items="items.map(user => {
            user.creationDate = formatDate(user.creationDate)
            user.updateDate = formatDate(user.updateDate)
            user.activated = user.activated ? 'Yes' : 'No'
            user.diabled = user.disabled ? 'Yes' : 'No'
            user.newslettersSubscribed = user.newslettersSubscribed ? 'Yes' : 'No'
            return user
        })" @click:row="onRowClick">
            <template v-slot:top>
                <div class="table-search-header">
                    <v-text-field
                            v-model="search"
                            append-icon="mdi-magnify"
                            label="Search by login, first name or last name"
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
        name: 'UserTable',
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