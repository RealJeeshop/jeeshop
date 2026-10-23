<template>
    <div class="field-container">
        <v-select :items="items"
                  item-title="text"
                  item-value="value"
                  :label="label"
                  v-model="selectedValue"
                  @update:model-value="update"
                  :rules="rules"
                  :placeholder="placeholder" />
    </div>
</template>

<script>
    export default {
        name: 'Select',
        props: {
            label: String,
            name: String,
            items: Array,
            placeholder: String,
            value: [String, Number],
            rules: Array
        },
      data() {
        return { selectedValue: this.value }
      },
      watch: {
        value(value) {
          this.selectedValue = value
        }
      },
      methods: {
        update() {
          this.$emit('on-update', {key: this.name, value: this.selectedValue})
        }
      }
    }
</script>