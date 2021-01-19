<template>
    <div class="item-edit-container">
        <div class="header">
            <h2 v-if="itemId">Product details</h2>
            <h2 v-else>Add new {{ itemType.slice(0, -1) }}</h2>
            <span @click="close" class="close-icon"></span>
        </div>

        <v-form class="form-container" ref="form">
          <div class="default-fields-container column">
            <Input label="Name" name="name" :value="item.name" :rules="required" @on-update="update" />
            <Input label="Description" name="description" placeholder="description"
                   :value="item.description" @on-update="update" />

            <div class="flex one-half">
              <DateField name="startDate" label="Start Date" placeholder="Choose visibility date"
                         :value="item.startDate" @on-update="update" />

              <DateField name="endDate" label="End Date" placeholder="Choose visibility end date"
                         :value="item.endDate" @on-update="update" />
            </div>
            <PresentationTable :value="presentations" @update-locale="onSelectLocale"/>

          </div>

          <div v-if="itemType === 'stores'">
            <RelationshipsTable label="Catalogs" itemType="catalogs"
                                :values="item.catalogs"
                                @open-edition="openRelationshipEdition" />
          </div>

          <div v-if="itemType === 'catalogs'">
            <RelationshipsTable label="Root categories" itemType="categories"
                                :values="item.rootCategories"
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
                                :values="item.discounts"/>

          </div>

          <div v-else-if="itemType === 'products'">
            <RelationshipsTable label="Child SKUs" itemType="skus"
                                @open-edition="openRelationshipEdition"
                                :values="item.childSKUs"/>

            <RelationshipsTable label="Product discounts" itemType="discounts"
                                @open-edition="openRelationshipEdition"
                                :values="item.discounts"/>
          </div>

          <div v-else-if="itemType === 'categories'">
            <RelationshipsTable itemType="categories"
                                label="Child Categories"
                                @open-edition="openRelationshipEdition"
                                :values="item.childCategories" />

            <RelationshipsTable label="Child products"
                                itemType="products"
                                @open-edition="openRelationshipEdition"
                                :values="item.childProducts"/>
          </div>

          <div v-else-if="itemType === 'discounts'">
            <div class="fields-container">
              <Select label="Applicable to..." :items="applyTarget" :rules="required" :value="item.applicableTo"/>
              <Input label="Voucher code" :value="item.voucherCode" placeholder="Enter voucher code" hint="Voucher code used by customers"/>
              <Select label="Type" :items="discountTypes"  :rules="required" :value="item.type" />
              <Input label="Value" :value="item.discountValue" placeholder="Enter a number..." hint="Discount value (rate or amount)"/>
              <Select label="Trigger threshold rules" :items="thresholdRules" :value="item.triggerRule"/>
              <Input label="Threshold" :value="item.triggerValue" placeholder="Enter a number..." hint="Trigger threshold (amount or quantity)"/>
              <Input label="Number of use per customer" :value="item.usesPerCustomer" placeholder="Enter a number ..."/>
              <v-checkbox label="Cumulative" :value="item.uniqueUse" />
            </div>
          </div>


          <v-btn color="primary" elevation="2" @click="saveItem()">Save</v-btn>
        </v-form>

        <LocaleEdition v-if="showLocaleEdition"
                       :open="showLocaleEdition"
                       :data="item.availableLocales ? item.availableLocales[this.locale] : null"
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
import {mapState} from 'vuex'
import Input from "../components/inputs/Input";
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
        components: {RelationshipsTable, PresentationTable, LocaleEdition, RelationshipsEdition, Select, Input, DateField},
        data() {
            return {
                itemType: undefined,
                itemId: undefined,
                editedRelationshipType: undefined,
                locale: undefined,
                showLocaleEdition: false,
                showRelationshipEdition: false,
                selectedRelationships: [],

                presentations: {},

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
                yesNo: ["Yes", "No"],
                required: [
                  value => !!value || this.$t("common.required"),
                ],
            }
        },
        computed: {
            ...mapState({
                item(state) {
                  let find = _.find(state.catalogs[this.itemType], item => item.id === this.itemId);

                  if (find) {

                    this.presentations = {
                      itemType: this.itemType,
                      itemId: this.itemId,
                      availableLocales: find.localizedPresentation
                    }

                    return _.cloneDeep(find)

                  } else return {}
                },
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

                if (this.$refs.form.validate()) {

                  this.$store.dispatch('catalogs/upsert', {itemType: this.itemType, item: this.item})
                  this.close()
                }
            },
            update(field) {
                this.item[field.key] = field.value
            },
            formatDate(date) {
                return DateUtils.formatDate(date)
            },
            onSelectLocale(locale) {
                this.locale = locale
                this.$store.dispatch("catalogs/getPresentation", {
                  itemType: this.itemType,
                  itemId: this.itemId,
                  locale: locale
                })

                this.showLocaleEdition = true
            },
            saveLocale(presentation) {
                this.showLocaleEdition = false
                this.$store.dispatch("catalogs/attachPresentation", {
                  itemType: this.itemType,
                  itemId: this.itemId,
                  presentation: presentation
                })

              let existingLocales = this.presentations.availableLocales ? this.presentations.availableLocales : []
              this.presentations = {
                itemType: this.itemType,
                itemId: this.itemId,
                availableLocales: [presentation.locale].concat(existingLocales)
              }
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
                this.itemType = this.$route.params.itemType || 'products'
                this.itemId = parseInt(this.$route.params.id)
                if (this.itemId) {
                    this.$store.dispatch('catalogs/getItemById', {itemType: this.itemType, itemId: this.itemId})
                }
            }
        },
        created() {

            this.fetchItem()
        }


    }
</script>

<style lang="scss" scoped>

    button {
        margin-top: 2em;
    }
</style>