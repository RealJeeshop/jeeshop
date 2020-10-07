<template>
    <div class="table-container">
        <div class="table-header">
            <div>Show 10 elements per page</div>
            <div>
                <input v-model="searchText" placeholder="Search by item id, name or description" />
                <i class="fa fa-2x fa-plus" />
            </div>
        </div>
        <table class="table-striped">
            <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Visible</th>
                <th>Disabled</th>
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
        </table>
    </div>

</template>

<script>

    export default {
        name: "Table",
        props: {
            items: Array[Object],
            of: String
        },
        data: () => {
            return {
                searchText: ""
            }
        },
        methods: {
            onRowClick(id) {
                this.$router.push(`${this.of}/${id}`)
            }
        },
    }
</script>

<style lang="scss" scoped>

    table {
        width: 100%;
        max-width: 100%;
        margin-bottom: 20px;
        background-color: transparent;
        border-spacing: 0;
        border-collapse: collapse;

        text-align: left;
    }

    .table-striped>tbody>tr:nth-of-type(odd) {
        background-color: #f9f9f9;
    }

    table>tbody>tr>td, table>tbody>tr>th,
    table>tfoot>tr>td, table>tfoot>tr>th,
    table>thead>tr>td, table>thead>tr>th {
        padding: 5px;
        line-height: 1.42857143;
        vertical-align: top;
        border-top: 1px solid #ddd;
    }

    tr:hover {
        background-color: #f9f9f9;
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