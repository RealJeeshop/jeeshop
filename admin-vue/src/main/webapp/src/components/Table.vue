<template>
    <div class="table-container">
        <div class="table-header">
            <div>Show 10 elements per page</div>
            <div>
                <input v-model="searchText" placeholder="Search by item id, name or description" />
                <i class="fa fa-2x fa-plus" />
            </div>
        </div>
        <v-simple-table>
            <thead>
                <tr>
                    <th class="text-left">Name</th>
                    <th class="text-left">Description</th>
                    <th class="text-left">Start Date</th>
                    <th class="text-left">End Date</th>
                    <th class="text-left">Visible</th>
                    <th class="text-left">Disabled</th>
                </tr>
            </thead>
            <tbody>
                <tr class="clickable" v-for="item in items" :key="item.id" @click="onRowClick(item.id)">
                    <td>{{ item.name }}</td>
                    <td>{{ item.description }}</td>
                    <td>{{ item.startDate }}</td>
                    <td>{{ item.endDate }}</td>
                    <td>{{ item.visible ? 'Yes' : 'No' }}</td>
                    <td>{{ item.disabled ? 'Yes' : 'No'}}</td>
                </tr>
            </tbody>
        </v-simple-table>
    </div>

</template>

<script>

    export default {
        name: "Table",
        props: {
            items: Array[Object],
            onItemSelected: Function,
            of: String
        },
        data: () => {
            return {
                searchText: ""
            }
        },
        methods: {
            onRowClick(id) {
                console.log('id : ' + JSON.stringify(id))
               this.$emit('item-selected', id)
            }
        },
    }
</script>

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