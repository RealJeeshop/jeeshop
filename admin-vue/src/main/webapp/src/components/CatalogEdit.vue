<template>
        <div class="catalog-edit-container">
            <div class="header">
                <h2 v-if="itemId">Product details</h2>
                <h2 v-else>Add new {{ itemType }}</h2>
                <span @click="close()" class="close-icon"></span>
            </div>

            <div class="form-container">
                <div class="default-field-container">
                    <Input label="Name" name="name" :value="item.name" @on-update="update" />
                    <Textarea label="Description" :value="item.description" name="description" placeholder="description" @on-update="update" />
                    <div class="flex one-half">
                        <DateField :value="item.startDate" name="startDate" label="Start Date" @on-update="update" />
                        <DateField :value="item.endDate" name="endDate" label="End Date" @on-update="update" />
                    </div>
                    <PresentationTable />
                </div>

                <div v-if="itemType === 'catalogs'">
                    <RelationshipsTable label="Root categories"/>
                </div>
                <div class="field-containers" v-else-if="itemType === 'skus'">

                    <Input label="Reference" :value="item.name" placeholder=""/>
                    <Input label="Price" :value="item.name" placeholder=""/>

                    <Select label="Currency" :items="currencies" />

                    <Input label="Quantity" :value="item.name" placeholder=""/>
                    <Input label="Threshold" :value="item.name" placeholder=""/>

                    <RelationshipsTable label="SKU discounts"/>
                </div>
                <div v-else-if="itemType === 'products'">
                    <RelationshipsTable label="Child SKUs"/>
                    <RelationshipsTable label="Product discounts"/>
                </div>

                <div v-else-if="itemType === 'categories'">
                    <RelationshipsTable label="Child Categories" />
                    <RelationshipsTable label="Child products" />
                </div>

                <div class="field-containers" v-else-if="itemType === 'discounts'">
                    <Select label="Applicable to..." :items="applyTarget" />
                    <Input label="Voucher code" :value="item.name" placeholder="Enter voucher code" hint="Voucher code used by customers"/>
                    <Select label="Type" :items="discountTypes" />
                    <Input label="Value" :value="item.name" placeholder="Enter a number..." hint="Discount value (rate or amount)"/>
                    <Select label="Trigger threshold rules" :items="thresholdRules" />
                    <Input label="Threshold" :value="item.name" placeholder="Enter a number..." hint="Trigger threshold (amount or quantity)"/>
                    <Input label="Number of use per customer" :value="item.name" placeholder="Enter a number ..."/>
                    <Input label="Cumulative" :value="item.name" placeholder=""/>
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

    export default {
        name: 'CatalogEdit',
        components: {RelationshipsTable, PresentationTable, Select, Input, Textarea, DateField},
        props: {
            itemType: String,
            itemId: Number,
        },
        data: () => {
            return {
                visible: 'visible',
                date: new Date(),
                disabled: false,
                currencies: ["EUR", "USD"],
                applyTarget: ["An order", "An item"],
                discountTypes: ["Discount rate", "Order amount discount", "Shipping fee amount discount"],
                thresholdRules: ["Specific quantity", "Specific price", "Number of orders (1 means first)"],
                yesNo: ["Yes", "No"]
            }
        },
        computed: {
            item() {
                return this.itemId ? this.$store.getters['catalogs/getById'](this.itemId, this.itemType) : {}
            },
        },
        methods: {
            close() {
              this.$emit('on-close')
            },
            saveItem() {
                this.$store.dispatch('catalogs/upsert', {itemType: this.itemType, item: this.item})
            },
            update(field) {
                this.item[field.key] = field.value
            },
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

    .form-container {
        display: flex;
        flex-direction: column;
        background-color: #FFFFFF;
        padding: 3em;
        margin-right: 4em;
        margin-left: 4em;

        box-shadow: 0 3px 6px rgba(0,0,0,0.06);
    }

    .default-field-container, .field-containers {
        display: flex;
        flex-wrap: wrap;
        flex-direction: column;
    }

    .field-container {
        margin-right: 1em;

    }

    .flex {
        display: flex;

        .one-half {
            flex: 0.5;
        }
    }

    button {
        margin-top: 2em;
    }
</style>