<template>

    <div class="item-edit-container">
      <div class="header">
        <h2>{{ $t(`catalogs.${itemType}.create.title`) }}</h2>
        <span @click="close" class="close-icon"></span>
      </div>

      <div class="form-container">

        <v-form ref="form">
          <v-flex>
            <Input label="Name" name="name" :value="name" :rules="required" @on-update="update"/>
            <Input label="Description" :value="description" name="description" @on-update="update"/>
          </v-flex>

          <v-flex flex-column>
            <v-flex align-center justify-space-between>
              <strong>Le produit est-il limité dans le temps ?</strong>
              <v-switch v-model="isLimitedInTime" :label="isLimitedInTime ? '' : ''" inset/>
            </v-flex>
            <v-flex v-if="isLimitedInTime" flex-wrap>
              <DateField name="startDate" label="Start Date" placeholder="Choose visibility date"
                         :value="startDate" @on-update="update"/>

              <DateField name="endDate" label="End Date" placeholder="Choose visibility end date"
                         :value="endDate" @on-update="update"/>
            </v-flex>
          </v-flex>

          <v-flex v-if="itemType === 'skus'" flex-wrap>
            <Input label="Name" name="skuName" :value="skuName" :rules="required" @on-update="update"/>
            <Input label="Reference" name="reference" :value="reference"  @on-update="update"/>
            <Input label="Quantity" name="quantity" :rules="required" :value="quantity" @on-update="update"/>

            <Select label="Currency" name="currency" :items="currencies" :rules="required" :value="currency" @on-update="update"/>
            <Input label="Price" name="price" :value="price" :rules="required" @on-update="update"/>

            <Input label="Threshold" name="threshold" :value="threshold"
                   placeholder="when the threshold is reached it will send you a notification" @on-update="update"/>
          </v-flex>

          <v-flex v-if="itemType === 'discounts'" flex-wrap>

            <Select label="Applicable to..." :items="applyTarget" :rules="required" :value="discount.applicableTo"/>
            <Input label="Voucher code" :value="discount.voucherCode" placeholder="Enter voucher code" hint="Voucher code used by customers"/>
            <Select label="Type" :items="discountTypes"  :rules="required" :value="discount.type" />
            <Input label="Value" :value="discount.discountValue" placeholder="Enter a number..." hint="Discount value (rate or amount)"/>
            <Select label="Trigger threshold rules" :items="thresholdRules" :value="discount.triggerRule"/>
            <Input label="Threshold" :value="discount.triggerValue" placeholder="Enter a number..." hint="Trigger threshold (amount or quantity)"/>
            <Input label="Number of use per customer" :value="discount.usesPerCustomer" placeholder="Enter a number ..."/>
            <v-checkbox label="Cumulative" :value="discount.uniqueUse" />
          </v-flex>

          <v-flex flex-column>
            <v-flex align-center justify-space-between>
              <strong>Ajouter une présentation ?</strong>
              <v-switch v-model="addPresentation" inset/>
            </v-flex>
            <v-flex v-if="addPresentation" flex-column>
              <div class="fields-container">
                <Input label="Name" name="displayName" :value="presentation.displayName" :rules="required" @on-update="updatePresentation" />
                <Input label="Promotion text" name="promotion" :value="presentation.promotion" @on-update="updatePresentation"/>
                <Textarea label="Short description" name="shortDescription" :value="presentation.shortDescription" @on-update="updatePresentation" />
                <Textarea label="Medium description" name="mediumDescription" :value="presentation.mediumDescription" @on-update="updatePresentation" />
                <Textarea label="Long description" name="longDescription" :value="presentation.longDescription" @on-update="updatePresentation"/>
              </div>
              <strong>Associer du contenu multimédia</strong>
              <div class="fields-container">
                <FileInput label="Thumbnail" name="thumbnail" :value="presentation.thumbnail" @on-update="updatePresentation" />
                <FileInput label="Small Image" name="smallImage" :value="presentation.smallImage" @on-update="updatePresentation" />
                <FileInput label="Large Image" name="largeImage" :value="presentation.largeImage" @on-update="updatePresentation" />
                <FileInput label="Video" name="video" :value="presentation.video" @on-update="updatePresentation" />
              </div>
            </v-flex>
          </v-flex>

          <div v-if="itemType === 'products'">
            <v-flex flex-column>
              <v-flex align-center justify-space-between>
                <strong>Ajouter un élément de stock</strong>
                <v-switch v-model="addProductSKU" inset/>
              </v-flex>
              <v-flex v-if="addProductSKU" flex-wrap>
                <Input label="Name" name="skuName" :value="skuName" :rules="required" @on-update="update"/>
                <Input label="Reference" name="reference" :value="reference"  @on-update="update"/>
                <Input label="Quantity" name="quantity" :rules="required" :value="quantity" @on-update="update"/>

                <Select label="Currency" name="currency" :items="currencies" :rules="required" :value="currency" @on-update="update"/>
                <Input label="Price" name="price" :value="price" :rules="required" @on-update="update"/>

                <Input label="Threshold" name="threshold" :value="threshold"
                       placeholder="when the threshold is reached it will send you a notification" @on-update="update"/>
              </v-flex>

              <AutocompleteInput :items="categories"
                                 label="Select one or more categories"
                                 title="Associer des catégories ?"
                                 @on-update="updateSelectedCategories"/>

              <AutocompleteInput :items="discounts"
                                 label="Select one or more discounts"
                                 title="Associer des réductions ?"
                                 @on-update="updateSelectedDiscounts"/>

            </v-flex>
          </div>

          <div v-if="itemType === 'categories'">

            <AutocompleteInput :items="categories"
                               label="Select one or more categories"
                               title="Associer des catégories ?"
                               @on-update="updateSelectedCategories"/>

            <AutocompleteInput :items="products"
                               label="Select one or more products"
                               title="Associer des produits ?"
                               @on-update="updateSelectedProducts"/>

          </div>

          <div v-if="itemType === 'catalogs'">

            <AutocompleteInput :items="categories"
                               label="Select one or more categories"
                               title="Associer des catégories ?"
                               @on-update="updateSelectedCategories"/>

          </div>

          <div v-if="itemType === 'skus'">

            <AutocompleteInput :items="discounts"
                               label="Select one or more discount"
                               title="Associer des réduction ?"
                               @on-update="updateSelectedDiscounts"/>

          </div>
        </v-form>
        <v-btn color="primary" elevation="2" @click="saveItem()">Sauvegarder</v-btn>
      </div>
    </div>

</template>

<script>
import Input from "@/components/inputs/Input";
import Select from "@/components/inputs/Select";
import DateField from "@/components/inputs/DateField";
import FileInput from "@/components/inputs/FileInput";
import Textarea from "@/components/inputs/Textarea";
import {mapState} from "vuex";
import AutocompleteInput from "@/components/inputs/AutocompleteInput";

export default {
  name: 'CreateProduct',
  components: {AutocompleteInput, Input, DateField, Select, FileInput, Textarea},
  data() {
    return {
      itemType: undefined,
      name: undefined,
      required: [
        value => !!value || this.$t("common.required"),
      ],
      skuName: undefined,
      description: undefined,
      startDate: undefined,
      endDate: undefined,
      isLimitedInTime: false,
      addProductSKU: false,
      addPresentation: false,
      reference: undefined,
      quantity: undefined,
      price: undefined,
      currency: undefined,
      threshold: undefined,
      presentation: {},
      discount: {},
      sku: {},
      selectedCategories: [],
      selectedDiscounts: [],
      selectedProducts: [],
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
      currencies: [{value: "EUR", text: "Euros"}, {value: "USD", text: "Dollars"}]
    }
  },
  computed: {
    ...mapState({
      categories(state) {
        return state.catalogs.categories
      },
      discounts(state) {
        return state.catalogs.discounts.filter(d => d.applicableTo === "ITEM")
      },
      products(state) {
        return state.catalogs.products
      }
    })
  },
  methods: {

    saveItem() {

      if (this.$refs.form.validate()) {

        this.$store.dispatch("catalogs/insertProductWithSku", {
          sku: this.addProductSKU ? {
            name: this.skuName,
            startDate: this.startDate,
            endDate: this.endDate,
            reference: this.reference,
            quantity: this.quantity,
            currency: this.currency,
            price: this.price,
            threshold: this.threshold
          } : null,
          product: {
            name: this.name,
            description: this.description,
            startDate: this.startDate,
            endDate: this.endDate,
          },
          presentation: this.addPresentation ? this.presentation : null,
          rootCategoriesIds: this.selectedCategories,
          discountsIds: this.selectedDiscounts
        })

        this.$router.back()
      }
    },
    update({key, value}) {
      this[key] = value
    },
    updatePresentation({key, value}) {
      this.presentation[key] = value
    },
    updateSelectedCategories(categories) {
      this.selectedCategories = categories
    },
    updateSelectedDiscounts(discounts) {
      this.selectedDiscounts = discounts
    },
    updateSelectedProducts(products) {
      this.selectedProducts= products
    },
    close() {
      this.$router.back()
    }
  },
  created() {
    this.itemType = this.$route.params.itemType
  },
  updated() {
    console.log("updating catalog edit")
  }
}
</script>

<style lang="scss" scoped>
  @import "edit-form";

  .error-block {
    text-align: center;
    padding: 1em;
    color: #c4372a;
  }
</style>