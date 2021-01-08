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

          <v-flex flex-column>
            <v-flex align-center justify-space-between>
              <strong>Ajouter une présentation</strong>
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

          <v-flex flex-column>
            <v-flex align-center justify-space-between>
              <strong>Associer une catégorie</strong>
              <v-switch v-model="addAssociatedItem" inset/>
            </v-flex>
            <v-flex v-if="addAssociatedItem" flex-column>
              <v-flex flex-column>
                <v-autocomplete
                    v-model="selectedCategories"
                    :items="categories"
                    label="Select one or categories"
                    item-text="name"
                    item-value="id"
                    multiple chips>
                  <template v-slot:selection="data">
                    <v-chip
                        v-bind="data.attrs"
                        :input-value="data.selected"
                        close
                        @click="data.select"
                        @click:close="remove('categories', data.item)">{{ data.item.name }}</v-chip>
                  </template>
                  <template v-slot:item="data">
                    <template v-if="typeof data.item !== 'object'">
                      <v-list-item-content v-text="data.item"></v-list-item-content>
                    </template>
                    <template v-else>
                      <v-list-item-content>
                        <v-list-item-title v-html="data.item.name"></v-list-item-title>
                      </v-list-item-content>
                    </template>
                  </template>
                </v-autocomplete>
              </v-flex>
            </v-flex>
          </v-flex>

          <v-flex flex-column>
            <v-flex align-center justify-space-between>
              <strong>Associer un réduction ?</strong>
              <v-switch v-model="addDiscount" inset/>
            </v-flex>
            <v-flex v-if="addDiscount" flex-wrap>

              <v-autocomplete
                  v-model="selectedDiscounts"
                  :items="discounts"
                  label="Select one or more discounts"
                  item-text="name"
                  item-value="id"
                  multiple chips>
                <template v-slot:selection="data">
                  <v-chip v-bind="data.attrs" :input-value="data.selected" @click="data.select"
                          @click:close="remove('discounts', data.item)" close>
                    {{ data.item.name }}
                  </v-chip>
                </template>
                <template v-slot:item="data">
                  <template v-if="typeof data.item !== 'object'">
                    <v-list-item-content v-text="data.item"></v-list-item-content>
                  </template>
                  <template v-else>
                    <v-list-item-content>
                      <v-list-item-title v-html="data.item.name"></v-list-item-title>
                    </v-list-item-content>
                  </template>
                </template>
              </v-autocomplete>

            </v-flex>
          </v-flex>

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

export default {
  name: 'CreateProduct',
  components: {Input, DateField, Select, FileInput, Textarea},
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
      addPresentation: false,
      addAssociatedItem: false,
      addDiscount: false,
      reference: undefined,
      quantity: undefined,
      price: undefined,
      currency: undefined,
      threshold: undefined,
      presentation: {},
      selectedCategories: [],
      selectedDiscounts: [],
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
          rootCategoriesIds: this.addAssociatedItem ? this.selectedCategories : null,
          discountsIds: this.addDiscount ? this.selectedDiscounts : null
        })

        this.$router.push("/products/create/presentation")
      }
    },
    update({key, value}) {
      this[key] = value
    },
    updatePresentation({key, value}) {
      this.presentation[key] = value

    },
    close() {
      this.$router.back()
    },
    remove(itemType, item) {

      if (itemType === 'categories'){
        let index = this.selectedCategories.findIndex(c => c.id === item.id)
        this.selectedCategories.splice(index, 1)

      } else if (itemType === 'discounts') {
        let index = this.selectedDiscounts.findIndex(c => c === item.id)
        this.selectedDiscounts.splice(index, 1)
      }
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