<template>
        <div class="catalog-edit-container">
            <div class="header">
                <h2>Product details</h2>
                <span @click="close()" class="close-icon">X</span>
            </div>

            <div class="form-container">
                <div class="default-field-container">
                    <Input label="Name" :value="item.name" placeholder="name"/>
                    <Textarea label="Description" :value="item.description" placeholder="description"  />
                    <DateField label="Start Date" />
                    <DateField label="End Date" />

                </div>
                <div v-if="itemType === 'catalogs'">
                    <div class="field-container">
                        <label>
                            <span>Relationships</span>
                            <v-expansion-panels>
                                <v-expansion-panel eleva>
                                    <v-expansion-panel-header>
                                        Root Categories
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-expansion-panels>
                        </label>
                    </div>
                </div>
                <div class="field-containers" v-else-if="itemType === 'skus'">

                    <Input label="Reference" :value="item.name" placeholder=""/>
                    <Input label="Price" :value="item.name" placeholder=""/>

                    <Select label="Currency" :items="currencies" />

                    <Input label="Quantity" :value="item.name" placeholder=""/>
                    <Input label="Threshold" :value="item.name" placeholder=""/>

                    <div class="field-container">
                        <label>
                            <span>Relationships</span>
                            <v-expansion-panels>
                                <v-expansion-panel>
                                    <v-expansion-panel-header>
                                        SKUs discount
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-expansion-panels>
                        </label>
                    </div>
                </div>
                <div v-else-if="itemType === 'products'">
                    <div class="field-container">
                        <label>
                            <span>Relationships</span>
                            <v-expansion-panels>
                                <v-expansion-panel>
                                    <v-expansion-panel-header>
                                        Child SKUs
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-expansion-panels>
                        </label>
                    </div>
                    <div class="field-container">
                        <label>
                            <span>Discounts</span>
                            <v-expansion-panels>
                                <v-expansion-panel>
                                    <v-expansion-panel-header>
                                        Products discounts
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-expansion-panels>
                        </label>
                    </div>
                </div>

                <div v-else-if="itemType === 'categories'">
                    <div class="field-container">
                        <label>
                            <span>Relationships</span>
                            <v-expansion-panels>
                                <v-expansion-panel>
                                    <v-expansion-panel-header>
                                        Child Categories
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-expansion-panels>
                        </label>
                    </div>
                    <div class="field-container">
                        <label>
                            <span>Relationships</span>
                            <v-expansion-panels>
                                <v-expansion-panel>
                                    <v-expansion-panel-header>
                                        Child products
                                    </v-expansion-panel-header>
                                    <v-expansion-panel-content>
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                    </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-expansion-panels>
                        </label>
                    </div>
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

                <v-btn color="primary" elevation="2">Save</v-btn>
            </div>

        </div>
</template>

<script>

    import Input from "../components/inputs/Input";
    import Textarea from "../components/inputs/Textarea";
    import DateField from "../components/inputs/DateField";
    import Select from "../components/inputs/Select";

    export default {
        name: 'CatalogEdit',
        components: {Select, Input, Textarea, DateField},
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
                return this.$store.getters['catalogs/getById'](this.itemId, this.itemType)
            },
        },
        methods: {
          close() {
              this.$router.go(-1)
          }
        }

    }
</script>

<style lang="scss" scoped>

    .catalog-edit-container {
        background-color: #F8F8F8;
    }

    .close-icon {

        cursor: pointer;
        font-size: x-large;
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
        flex-direction: row;
        flex-wrap: wrap;
    }

    .field-container {
        min-width: 40%;
        margin-right: 1em;

    }

</style>