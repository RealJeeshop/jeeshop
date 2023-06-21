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
                :error="error"
                :readonly="readonly"
                :rules="rules"
                :type="insideType"
                hide-details="auto"/>
        </label>
    </div>
</template>

<script>
    export default {
        name: 'Input',
        data() {
            return {
              insideValue: this.value ? this.value : this.value,
              insideType: this.type ? this.type : "text"
            }
        },
        props: {
          label: String,
          value: [String, Number, Object],
          placeholder: String,
          name: String,
          hint: String,
          disabled: Boolean,
          dense: Boolean,
          error: Boolean,
          readonly: Boolean,
          rules: Array,
          type: String
        },
        methods: {
            update() {
              if (this.value && this.value.id) {
                this.$emit('on-update', {key: this.name, id: this.value.id, value: this.insideValue})
              } else {
                this.$emit('on-update', {key: this.name, value: this.insideValue})
              }
            }
        }
    }
</script>