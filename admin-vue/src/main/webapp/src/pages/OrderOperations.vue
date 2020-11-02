<template>
    <div class="page-content column padded-xl">
        <h1>Process Payed orders</h1>
        <Input label="SKU Id" placeholder="Filter by SKU id ..." hint="Filter orders with items matching given SKU identifier" />
        <v-btn color="primary" @click="exportCSV">Export CSV</v-btn>
    </div>
</template>

<script>
    import Input from "../components/inputs/Input";
    export default {
        name: 'OrderOperations',
        components: {Input},
        methods: {
            exportCSV() {
                console.log("exporting CSV")
            }
        }
    }
</script>