<template>

    <div class="item-edit-container">
      <div class="header">
        <h2>{{ $t("catalogs.products.create.title") }}</h2>
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
          </v-flex>
        </v-form>
        <v-btn color="primary" elevation="2" @click="saveItem()">Ajouter une présentation</v-btn>
      </div>
    </div>

</template>

<script>
import Input from "../components/inputs/Input";
import Select from "../components/inputs/Select";
import DateField from "../components/inputs/DateField";

export default {
  name: 'CreateProduct',
  components: {Input, DateField, Select},
  data() {
    return {
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
      reference: undefined,
      quantity: undefined,
      price: undefined,
      currency: undefined,
      threshold: undefined,
      currencies: [{value: "EUR", text: "Euros"}, {value: "USD", text: "Dollars"}]
    }
  },
  methods: {

    saveItem() {

      if (this.$refs.form.validate()) {

        this.$store.dispatch("catalogs/insertProductWithSku", {
          sku: {
            name: this.skuName,
            startDate: this.startDate,
            endDate: this.endDate,
            reference: this.reference,
            quantity: this.quantity,
            currency: this.currency,
            price: this.price,
            threshold: this.threshold
          },
          product: {
            name: this.name,
            description: this.description,
            startDate: this.startDate,
            endDate: this.endDate,
          }
        })

        this.$router.push("/products/create/presentation")
      }
    },
    update({key, value}) {
      this[key] = value
    },
    close() {

    }
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