<template>
    <div class="table-container">
        <v-data-table :search="search" :headers="normalizedHeaders" :items="items.map(item => {
            item.visible = item.visible ? 'Yes' : 'No'
            item.disabled = item.disabled ? 'Yes' : 'No'
            item.startDate = formatDate(item.startDate)
            item.endDate = formatDate(item.endDate)
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
            <template v-slot:[actionsSlot]="{ item }">
                <slot name="actions" :item="item"></slot>
            </template>
        </v-data-table>
    </div>

</template>

<script>

    import DateUtils from '../lib/dateUtils'
    import { normalizeDataTableHeaders } from '../lib/vuetifyUtils'

    export default {
        name: "CatalogItemTable",
        props: {
            headers: Array,
            items: Array,
            of: String
        },
        data: () => {
            return {
                search: "",
                actionsSlot: "item.actions"
            }
        },
        computed: {
            normalizedHeaders() {
                return normalizeDataTableHeaders(this.headers)
            }
        },
        methods: {
            onRowClick(_, { item }) {
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

    .table-container {
        background-color: white;
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