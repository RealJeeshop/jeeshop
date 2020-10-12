<template>
    <div class="field-container">
        <label>
            <v-text-field
                    :label="label"
                    v-model="insideValue"
                    :placeholder="placeholder"
                    :hint="hint"
                    @input="update"/>
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
            value: String,
            placeholder: String,
            name: String,
            hint: String
        },
        methods: {
            update() {
                this.$emit('on-update', {key: this.name, value: this.insideValue})
            }
        }
    }
</script>