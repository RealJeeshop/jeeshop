<template>

    <div class="item-edit-container">
      <div class="header">
        <h2>Ajouter un nouveau produit</h2>
        <span @click="close" class="close-icon"></span>
      </div>

      <div class="form-container">

        <v-flex>
          <Input label="Name" name="name" :value="name" @on-update="updateName"/>
          <Input label="Description" :value="description" name="description" @on-update="update"/>
        </v-flex>

        <!--            <v-flex>-->
        <!--              <span>NB : this is for internal use. Only <strong>presentations</strong> will be displayed on the e-shop</span>-->
        <!--            </v-flex>-->

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
            <Input label="Name" name="skuName" :value="skuName" placeholder="" @on-update="updateName"/>
            <Input label="Reference" name="reference" :value="reference" placeholder="" @on-update="update"/>
            <Input label="Quantity" name="quantity" :value="quantity" placeholder="" @on-update="update"/>

            <Select label="Currency" name="currency" :items="currencies" :value="currency" @on-update="update"/>
            <Input label="Price" name="price" :value="price" placeholder="" @on-update="update"/>

            <Input label="Threshold" name="threshold" :value="threshold"
                   placeholder="when the threshold is reached it will send you a notification" @on-update="update"/>
          </v-flex>
        </v-flex>

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
    },
    update({key, value}) {
      this[key] = value
    },
    updateName(newValue) {
      let skuName = newValue.value && newValue.value !== "" ? newValue.value + "-" : "";
      this.skuName = skuName;
      this.name = newValue.value
    },
    close() {

    }
  }
}
</script>

<style lang="scss" scoped>
  @import "edit-form";
</style>