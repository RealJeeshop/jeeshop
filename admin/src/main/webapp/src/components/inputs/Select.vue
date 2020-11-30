<template>
    <div class="field-container">
        <v-select :items="items"
                  :label="label"
                  v-model="value"
                  :placeholder="placeholder" />
    </div>
</template>

<script>
    export default {
        name: 'Select',
        props: {
            label: String,
            items: Array[Object],
            placeholder: String,
            value: String
        },
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