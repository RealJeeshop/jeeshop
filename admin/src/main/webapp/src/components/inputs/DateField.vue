<template>
    <div class="field-container">
        <label>
            <v-dialog v-model="show" max-width="500">
                <template #activator="{ props }">
                    <v-text-field
                            :label="label"
                            :model-value="insideValue"
                            :placeholder="placeholder"
                            readonly
                            v-bind="props"/>
                </template>
                <v-date-picker v-if="show"
                               :full-width="true"
                               elevation="12"
                               v-model="insideValue">

                    <v-spacer></v-spacer>
                    <v-btn variant="text" color="primary" @click="show = false">Cancel</v-btn>
                    <v-btn variant="text" color="primary" @click="update">OK</v-btn>
                </v-date-picker>

            </v-dialog>
        </label>
    </div>
</template>

<script>

    import DateUtils from "../../lib/dateUtils";

    export default {
        name: 'DateField',
        data() {
          return {
              show: false,
              insideValue: this.formatDate(this.value)
          }
        },
        props: {
            label: String,
            name: String,
            value: String,
            placeholder: String
        },
        methods: {
            togglePicker() {
                this.show = !this.show
            },
            formatDate(date) {
                return DateUtils.formatDate(date)
            },
            update() {
                this.$emit('on-update', {key: this.name, value: this.insideValue})
                this.show = false
            }
        }
    }
</script>