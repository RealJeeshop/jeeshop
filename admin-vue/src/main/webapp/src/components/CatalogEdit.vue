<template>
        <div class="catalog-edit-container">
            <div class="header">
                <h2 v-if="itemId">Product details</h2>
                <h2 v-else>Add new {{ itemType.slice(0, -1) }}</h2>
                <span @click="close()" class="close-icon"></span>
            </div>

            <div class="form-container">
                <div class="default-fields-container column">
                    <Input label="Name" name="name" :value="item.name" @on-update="update" />
                    <Textarea label="Description" :value="item.description" name="description" placeholder="description" @on-update="update" />
                    <div class="flex one-half">
                        <DateField name="startDate" label="Start Date" placeholder="Choose visibility date"
                                   :value="formatDate(item.startDate)" @on-update="update" />

                        <DateField name="endDate" label="End Date" placeholder="Choose visibility end date"
                                   :value="formatDate(item.endDate)" @on-update="update" />
                    </div>
                </div>

                <div v-if="itemType === 'catalogs'">
                    <PresentationTable :value="item.localizedPresentation" />
                    <RelationshipsTable label="Root categories" itemType="categories" :values="item.rootCategoriesIds"/>
                </div>

                <div v-else-if="itemType === 'skus'">
                    <div class="fields-container">

                        <Input label="Reference" :value="item.name" placeholder=""/>
                        <Input label="Quantity" :value="item.name" placeholder=""/>

                        <Input label="Price" :value="item.name" placeholder=""/>
                        <Select label="Currency" :items="currencies" />

                        <Input label="Threshold" :value="item.name" placeholder=""/>

                    </div>
                    <PresentationTable :value="item.localizedPresentation" />
                    <RelationshipsTable label="SKU discounts" itemType="discounts" :values="item.discountsIds"/>
                </div>

                <div v-else-if="itemType === 'products'">
                    <PresentationTable :value="item.localizedPresentation" />
                    <RelationshipsTable label="Child SKUs" itemType="skus" :values="item.childSKUsIds"/>
                    <RelationshipsTable label="Product discounts" itemType="discounts" :values="item.discountsIds"/>
                </div>

                <div v-else-if="itemType === 'categories'">
                    <PresentationTable :value="item.localizedPresentation" />
                    <RelationshipsTable label="Child Categories" itemType="categories" :values="item.childCategoriesIds" />
                    <RelationshipsTable label="Child products" itemType="products" :values="item.childProductsIds"/>
                </div>

                <div v-else-if="itemType === 'discounts'">
                    <div class="fields-container">
                        <Select label="Applicable to..." :items="applyTarget" />
                        <Input label="Voucher code" :value="item.name" placeholder="Enter voucher code" hint="Voucher code used by customers"/>
                        <Select label="Type" :items="discountTypes" />
                        <Input label="Value" :value="item.name" placeholder="Enter a number..." hint="Discount value (rate or amount)"/>
                        <Select label="Trigger threshold rules" :items="thresholdRules" />
                        <Input label="Threshold" :value="item.name" placeholder="Enter a number..." hint="Trigger threshold (amount or quantity)"/>
                        <Input label="Number of use per customer" :value="item.name" placeholder="Enter a number ..."/>
                        <Input label="Cumulative" :value="item.name" placeholder=""/>
                    </div>
                    <PresentationTable :value="item.localizedPresentation" />
                </div>


                <v-btn color="primary" elevation="2" @click="saveItem()">Save</v-btn>
            </div>

        </div>
</template>

<script>

    import Input from "./inputs/Input";
    import Textarea from "./inputs/Textarea";
    import DateField from "./inputs/DateField";
    import Select from "./inputs/Select";
    import PresentationTable from "./PresentationsTable";
    import RelationshipsTable from "./RelationshipsTable";
    import DateUtils from '../lib/dateUtils'
    import {mapState} from 'vuex'
    import _ from "lodash";

    export default {
        name: 'CatalogEdit',
        components: {RelationshipsTable, PresentationTable, Select, Input, Textarea, DateField},
        data: () => {
            return {
                itemType: undefined,
                itemId: undefined,
                currencies: ["EUR", "USD"],
                applyTarget: ["An order", "An item"],
                discountTypes: ["Discount rate", "Order amount discount", "Shipping fee amount discount"],
                thresholdRules: ["Specific quantity", "Specific price", "Number of orders (1 means first)"],
                yesNo: ["Yes", "No"]
            }
        },
        methods: {
            close() {
              this.$router.back()
            },
            saveItem() {
                this.$store.dispatch('catalogs/upsert', {itemType: this.itemType, item: this.item})
                this.close()
            },
            update(field) {
                this.item[field.key] = field.value
            },
            formatDate(date) {
                return DateUtils.formatDate(date)
            }
        },
        computed: mapState({
            item(state) {
                console.log('state.catalogs[this.itemType] : ' + JSON.stringify(state.catalogs[this.itemType]))
                let find = _.find(state.catalogs[this.itemType], item => item.id === this.itemId);
                console.log('find : ' + JSON.stringify(find))
                return find ? find : {};
            }
        }),
        created() {

            console.log('this.$route.params : ' + JSON.stringify(this.$route.params))
            this.itemType = this.$route.params.itemType
            this.itemId = parseInt(this.$route.params.id)
            if (this.itemId) this.$store.dispatch('catalogs/getItemById', {itemType: this.itemType, itemId: this.itemId})
        }

    }
</script>

<style lang="scss" scoped>

    .catalog-edit-container {
        background-color: #efefef;
        flex: 2;
        border-left: 1px solid #e0e0e0;
    }

    .close-icon {

        cursor: pointer;
        position: relative;

        width: 32px;
        height: 25px;
        opacity: 0.3;
    }

    .close-icon:hover {
        opacity: 1;
    }

    .close-icon:before, .close-icon:after {
        position: absolute;
        left: 15px;
        content: ' ';
        height: 25px;
        width: 2px;
        background-color: #333;
    }
    .close-icon:before {
        transform: rotate(45deg);
    }

    .close-icon:after {
        transform: rotate(-45deg);
    }

    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 2em;
    }

    button {
        margin-top: 2em;
    }
</style>