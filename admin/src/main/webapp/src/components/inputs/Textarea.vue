<template>
    <div class="field-container">
            <v-textarea v-model="insideValue" :label="label" rows="3" @input="update"/>
    </div>
</template>

<script>
    export default {
        name: 'Textarea',
        data: function() {
          return {
              insideValue: this.value
          }
        },
        props: {
            label: String,
            value: String,
            placeholder: String,
            name: String

        },
        methods: {
            update() {
                this.$emit('on-update', {key: this.name, value: this.insideValue})
            }
        }
    }
</script>