<template>
    <div class="item-edit-container">
        <div class="header">
            <h2 v-if="itemId">Product details</h2>
            <h2 v-else>Add new {{ itemType.slice(0, -1) }}</h2>
            <span @click="close" class="close-icon"></span>
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
                <PresentationTable :value="localizedPresentation" @update-locale="onSelectLocale"/>

            </div>

            <div v-if="itemType === 'catalogs'">
                <RelationshipsTable label="Root categories" itemType="categories"
                                    :values="item.rootCategoriesIds"
                                    @open-edition="openRelationshipEdition" />
            </div>

            <div v-else-if="itemType === 'skus'">
                <div class="fields-container">

                    <Input label="Reference" :value="item.reference" placeholder=""/>
                    <Input label="Quantity" :value="item.quantity" placeholder=""/>

                    <Input label="Price" :value="item.price" placeholder=""/>
                    <Select label="Currency" :items="currencies" :value="item.currency"/>

                    <Input label="Threshold" :value="item.threshold" placeholder=""/>

                </div>
                <RelationshipsTable label="SKU discounts" itemType="discounts"
                                    @open-edition="openRelationshipEdition"
                                    :values="item.discountsIds"/>

            </div>

            <div v-else-if="itemType === 'products'">
                <RelationshipsTable label="Child SKUs" itemType="skus"
                                    @open-edition="openRelationshipEdition"
                                    :values="item.childSKUsIds"/>

                <RelationshipsTable label="Product discounts" itemType="discounts"
                                    @open-edition="openRelationshipEdition"
                                    :values="item.discountsIds"/>
            </div>

            <div v-else-if="itemType === 'categories'">
                <RelationshipsTable itemType="categories"
                                    label="Child Categories"
                                    @open-edition="openRelationshipEdition"
                                    :values="item.childCategoriesIds" />

                <RelationshipsTable label="Child products"
                                    itemType="products"
                                    @open-edition="openRelationshipEdition"
                                    :values="item.childProductsIds"/>
            </div>

            <div v-else-if="itemType === 'discounts'">
                <div class="fields-container">
                    <Select label="Applicable to..." :items="applyTarget" :value="item.applicableTo"/>
                    <Input label="Voucher code" :value="item.voucherCode" placeholder="Enter voucher code" hint="Voucher code used by customers"/>
                    <Select label="Type" :items="discountTypes" :value="item.type" />
                    <Input label="Value" :value="item.discountValue" placeholder="Enter a number..." hint="Discount value (rate or amount)"/>
                    <Select label="Trigger threshold rules" :items="thresholdRules" :value="item.triggerRule"/>
                    <Input label="Threshold" :value="item.triggerValue" placeholder="Enter a number..." hint="Trigger threshold (amount or quantity)"/>
                    <Input label="Number of use per customer" :value="item.usesPerCustomer" placeholder="Enter a number ..."/>
                    <v-checkbox label="Cumulative" :value="item.uniqueUse" />
                </div>
            </div>


            <v-btn color="primary" elevation="2" @click="saveItem()">Save</v-btn>
        </div>

        <LocaleEdition v-if="showLocaleEdition"
                       :open="showLocaleEdition"
                       :data="item.availableLocales[this.locale]"
                       @on-cancel="showLocaleEdition = false"
                       @on-save="saveLocale" />

        <RelationshipsEdition v-if="showRelationshipEdition"
                              :open="showRelationshipEdition"
                              :itemType="editedRelationshipType"
                              :selectedRelationships="selectedRelationships"
                              @on-cancel="showRelationshipEdition = false"
                              @on-save="saveRelationship"/>

    </div>
</template>

<script>

    import './edit-form.scss'
    import { mapState } from 'vuex'
    import Input from "../components/inputs/Input";
    import Textarea from "../components/inputs/Textarea";
    import DateField from "../components/inputs/DateField";
    import Select from "../components/inputs/Select";
    import PresentationTable from "../components/PresentationsTable";
    import RelationshipsTable from "../components/RelationshipsTable";
    import DateUtils from '../lib/dateUtils'
    import _ from "lodash";
    import LocaleEdition from "../components/LocaleEdition";
    import RelationshipsEdition from '../components/RelationshipsEdition'

    export default {
        name: 'CatalogEdit',
        components: {RelationshipsTable, PresentationTable, LocaleEdition, RelationshipsEdition, Select, Input, Textarea, DateField},
        data: () => {
            return {
                itemType: undefined,
                itemId: undefined,
                editedRelationshipType: undefined,
                showLocaleEdition: false,
                showRelationshipEdition: false,
                selectedRelationships: [],
                currencies: [{value: "EUR", text: "Euros"}, {value: "USD", text: "Dollars"}],
                applyTarget: [{value: "ORDER", text: "An order"}, {value: "ITEM", text: "An item"}],
                discountTypes: [{value: "DISCOUNT_RATE", text: "Discount rate"},
                    {value: "ORDER_DISCOUNT_AMOUNT", text: "Order amount discount"},
                    {value: "SHIPPING_FEE_DISCOUNT_AMOUNT", text: "Shipping fee amount discount"}
                ],
                thresholdRules: [
                    {value: "QUANTITY", text: "Specific quantity"},
                    {value: "AMOUNT", text: "Specific price"},
                    {value: "ORDER_NUMBER", text: "Number of orders (1 means first)"}
                ],
                yesNo: ["Yes", "No"]
            }
        },
        computed: {
            ...mapState({
                item(state) {

                    let find = _.find(state.catalogs[this.itemType], item => item.id === this.itemId);
                    return find ? _.cloneDeep(find) : {}
                },
                presentation(state) {

                    let item = _.find(state.catalogs[this.itemType], item => item.id === this.itemId);
                    return item && item.availableLocales && item.availableLocales[this.locale]
                        ? _.cloneDeep(item.availableLocales[this.locale])
                        : {}

                }
            }),
            localizedPresentation() {
                return {
                    itemId: this.itemId,
                    itemType: this.itemType,
                    availableLocales: this.item.localizedPresentation
                }
            }
        },
        watch: {
            '$route': 'fetchItem'
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
            },
            onSelectLocale(locale) {
                this.locale = locale
                this.showLocaleEdition = true
            },
            saveLocale() {
                this.showLocaleEdition = false
            },
            saveRelationship() {
                this.selectedRelationships = []
                this.showRelationshipEdition = false
            },
            openRelationshipEdition({itemType, ids}) {
                this.selectedRelationships = ids
                this.editedRelationshipType = itemType
                this.showRelationshipEdition = true
            },
            fetchItem() {
                this.itemType = this.$route.params.itemType
                this.itemId = parseInt(this.$route.params.id)
                if (this.itemId) {
                    this.$store.dispatch('catalogs/getItemById', {itemType: this.itemType, itemId: this.itemId})
                }
            }
        },
        created() {
            console.log("creating catalog edit")
            this.fetchItem()
        },
        updated() {
            console.log("updating catalog edit")
        }


    }
</script>

<style lang="scss" scoped>

    button {
        margin-top: 2em;
    }
</style>