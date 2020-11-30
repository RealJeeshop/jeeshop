<template>
    <div class="item-edit-container">
        <div class="header">
            <h2>Order details</h2>
            <span @click="close" class="close-icon"></span>
        </div>

        <div class="form-container">
            <div class="fields-container">
                <div class="field-container">
                    <label>Reference</label>
                    <span>{{order.reference}}</span>
                </div>
                <div class="field-container">
                    <label>Price</label>
                    <span>{{order.price}}</span>
                </div>
                <Input label="Payment transaction ID" placeholder="Payment transaction ID" :value="order.transactionId" readonly />
                <Input label="Payment transaction date"  :value="formatDate(order.paymentDate)" readonly />
            </div>

            <div class="fields-container half-width">
                <Input  label="Parcel tracking ID" placeholder="Parcel tracking ID" :value="order.parcelTrackingKey" />
            </div>

            <div class="fields-container">
                <Input label="Last modification date" :value="formatDate(order.updateDate)" disabled />
                <Input label="Creation date" :value="formatDate(order.creationDate)" disabled />
            </div>


            <div class="address-container">

                <AddressForm label="Delivery Address" :value="order.deliveryAddress" />
                <AddressForm label="Billing Address" :value="order.billingAddress" />

            </div>

            <v-btn color="primary" elevation="2" @click="saveOrder()">Save</v-btn>
        </div>
    </div>
</template>

<script>
    import './edit-form.scss'
    import Input from "../components/inputs/Input";
    import AddressForm from "../components/AddressForm";
    import {mapState} from "vuex";
    import _ from "lodash";
    import DateUtils from "../lib/dateUtils";

    export default {
        name: 'OrderEdit',
        components: {Input, AddressForm},
        props: {

        },
        data() {
          return {
              orderId: undefined
          }
        },
        computed: {
            ...mapState({
                order(state) {
                    let find = _.find(state.orders.orders, o => o.id === this.orderId);
                    return find ? _.cloneDeep(find) : {}
                },
            })
        },
        watch: {
            '$route': 'fetchOrder'
        },
        methods: {
            close() {
                this.$router.back()
            },
            fetchOrder() {
                this.orderId = parseInt(this.$route.params.id)
                this.$store.dispatch('orders/getById', this.orderId)
            },
            formatDate(date) {
                return DateUtils.formatDate(date)
            },
            saveOrder() {
                this.$router.back()
            }
        },
        created() {
            this.fetchOrder()
        }
    }
</script>

<style lang="scss" scoped>
   .order-edit-container {
       background-color: #efefef;
       flex: 2;
       border-left: 1px solid #e0e0e0;
       width: 100%;
   }

    .address-container {
        display: flex;
    }

</style>