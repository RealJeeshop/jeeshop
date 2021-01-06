<template>
    <div class="field-container">
        <label>
            <v-text-field
                    :label="label"
                    v-model="insideValue"
                    :placeholder="placeholder"
                    :hint="hint"
                    @input="update"
                    :disabled="disabled"
                    :dense="dense"
                    :readonly="readonly" />
        </label>
    </div>
</template>

<script>
    export default {
        name: 'Input',
        data() {
            return {
                insideValue: this.value
            }
        },
        props: {
            label: String,
            value: [String,Number],
            placeholder: String,
            name: String,
            hint: String,
            disabled: Boolean,
            dense: Boolean,
            readonly: Boolean
        },
        methods: {
            update() {
              console.log("ho")
                this.$emit('on-update', {key: this.name, value: this.insideValue})
            }
        }
    }
</script>