<template>
  <div class="page-content column">
    <div class="item-edit-container">
      <div class="header">
        <div>
          <h2>Attacher le produit</h2>
          <span>Votre produit doit être lié à une catégorie. Vous pouvez également lui associer des réductions.</span>
        </div>
        <span @click="close" class="close-icon"></span>
      </div>

      <div class="form-container">

        <v-flex flex-column>
          <strong>Associer une catégorie</strong>
          <v-autocomplete
              v-model="selectedCategories"
              :disabled="isUpdating"
              :items="categories"
              chips
              label="Select one or categories"
              item-text="name"
              item-value="name"
              multiple>
            <template v-slot:selection="data">
              <v-chip
                  v-bind="data.attrs"
                  :input-value="data.selected"
                  close
                  @click="data.select"
                  @click:close="remove(data.item)">{{ data.item.name }}</v-chip>
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

        <v-flex flex-column>
          <v-flex align-center justify-space-between>
            <strong>Associer un réduction ?</strong>
            <v-switch v-model="addDiscount" inset/>
          </v-flex>
          <v-flex v-if="addDiscount" flex-wrap>

            <v-autocomplete
                v-model="selectedCategories"
                :disabled="isUpdating"
                :items="discounts"
                chips
                label="Select one or more discounts"
                item-text="name"
                item-value="name"
                multiple>
              <template v-slot:selection="data">
                <v-chip v-bind="data.attrs"
                        :input-value="data.selected"
                        close
                        @click="data.select"
                        @click:close="remove(data.item)">{{ data.item.name }}</v-chip>
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

        <v-btn color="primary" elevation="2" @click="saveItem()">Sauvegarder</v-btn>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState} from 'vuex'

export default {
  name: 'LinkProduct',
  components: {},
  data() {
    return {
      selectedCategories: [],
      selectedDiscounts: [],
      addDiscount: false,
      presentation: {},
      isUpdating: false
    }
  },
  computed: {
    ...mapState({
      categories(state) {
        return state.catalogs.categories
      },
      discounts(state) {
        return state.catalogs.discounts.filter(d => {
          return d.applicableTo === "ITEM"
        })
      }
    })
  },
  methods: {
    update() {

    },
    close() {

    },
    saveItem() {
      this.$router.push("/create/product/link")
    },
    remove() {

    }
  }
}
</script>

<style lang="scss" scoped>
@import "edit-form";
</style>