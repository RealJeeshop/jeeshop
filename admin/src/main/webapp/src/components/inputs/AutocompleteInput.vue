<template>
  <v-flex flex-column>
    <v-flex align-center justify-space-between>
      <strong>{{ title }}</strong>
      <v-switch v-model="isEnabled" inset/>
    </v-flex>
    <v-flex v-if="isEnabled" flex-column>
      <v-flex flex-column>
        <v-autocomplete
            v-model="selectedItems"
            :items="items"
            :label="placeholder"
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
</template>

<script>
  export default {
    name: 'AutocompleteInput',
    props: {
      title: String,
      label: String,
      items: Array,
      placeholder: String
    },
    data() {
      return {
        selectedItems: [],
        isEnabled: false
      }
    },
    watch: {
      selectedItems(values) {
        this.$emit("on-update", values)
      }
    },
    methods: {
      remove(item) {
          let index = this.selectedItems.findIndex(c => c.id === item.id)
          this.selectedItems.splice(index, 1)
      }
    }
  }
</script>


<style>

</style>