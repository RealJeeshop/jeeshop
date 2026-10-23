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
            item-title="name"
            item-value="id"
            multiple chips>
          <template #selection="{ internalItem }">
            <v-chip closable @click:close="remove(internalItem.raw)">
              {{ internalItem.title }}
            </v-chip>
          </template>
          <template #item="{ internalItem, props }">
            <v-list-item v-bind="props" :title="internalItem.title"></v-list-item>
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
          const index = this.selectedItems.indexOf(item.id)
          if (index !== -1) this.selectedItems.splice(index, 1)
      }
    }
  }
</script>


<style>

</style>