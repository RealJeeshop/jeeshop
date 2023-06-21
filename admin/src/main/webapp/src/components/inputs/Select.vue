<template>
    <div class="field-container">
        <v-select :items="items"
                  :label="label"
                  v-model="value"
                  @input="update"
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
            items: Array[Object],
            placeholder: String,
            value: String,
            rules: Array
        },
      methods: {
        update() {
          this.$emit('on-update', {key: this.name, value: this.value})
        }
      }
        // data() {
        //     return {
        //         selected: this.value ? this.value : undefined
        //     }
        // },
        // watch: {
        //     value() {
        //         this.selected = this.value ? this.value : undefined
        //     }
        // }
    }
</script>