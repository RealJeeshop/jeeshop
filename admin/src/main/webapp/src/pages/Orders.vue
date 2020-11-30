<template>
    <div class="page-content column">
        <v-toolbar color="#484848" dark flat>
            <v-tabs class="sub-header-menu" align-with-title>
                <v-tabs-slider color="yellow"></v-tabs-slider>
                <v-tab to="/orders">Orders</v-tab>
                <v-tab to="/orders/operations">Operations</v-tab>
            </v-tabs>
        </v-toolbar>
        <div class="content">
            <OrderTable v-if="$route.path.indexOf('operations') === -1"
                        :headers="headers" :items="orders"
                        @item-selected="handleItemSelection" />

            <router-view />
        </div>
    </div>
</template>

<script>

    import OrderTable from "../components/OrderTable";
    import {mapState} from "vuex";
    import _ from "lodash";

    export default {
        name: 'Orders',
        components: { OrderTable },
        data() {
            return {
                showEditPanel: false,
                orderStatuses: [
                    "CREATED", "VALIDATED", "PAYMENT_VALIDATED", "CANCELLED,READY_FOR_SHIPMENT", "SHIPPED", "DELIVERED", "RETURNED"
                ]
            }
        },
        computed: {
            ...mapState({
                orders(state) {
                    let cloneDeep = _.cloneDeep(state.orders.orders);
                    console.log('cloneDeep : ' + JSON.stringify(cloneDeep))
                    return cloneDeep
                }
            }),
            headers() {
                let headers = [
                    {text: "Id", value: "id"},
                    {text: "Reference", value: "reference"},
                    {text: "Owner", value: "owner"},
                    {text: "login", value: "login"},
                    {text: "Status", value: "status"},
                    {text: "Creation Date", value: "creationDate"},
                    {text: "Last Modification", value: "updateDate"}
                ]

                if (this.showEditPanel) {
                    return headers.splice(0, 2)

                } else return headers
            }
        },
        methods: {
            handleItemSelection(id) {
                console.log('selected order id : ' + JSON.stringify(id))
                this.$router.push(`/orders/${id}`)
            }
        },
        created () {

            this.showEditPanel = !!this.$route.params.id;
            if (!this.showEditPanel) this.$store.dispatch('orders/getAll')
        },

        updated() {

            this.showEditPanel = !!this.$route.params.id;
        }
    }
</script>

<style lang="scss" scoped>
    .table-container {
        flex: 1;
    }
</style>