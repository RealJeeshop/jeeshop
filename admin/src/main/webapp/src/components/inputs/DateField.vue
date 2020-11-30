<template>
    <div class="field-container">
        <label>
            <v-dialog ref="dialog" :return-value.sync="insideValue" v-model="show" max-width="500">
                <template v-slot:activator="{ on, attrs }">
                    <v-text-field
                            @click="togglePicker()"
                            :label="label"
                            :value="insideValue"
                            :placeholder="placeholder"
                            v-bind="attrs" v-on="on"/>
                </template>
                <v-date-picker v-if="show"
                               :full-width="true"
                               elevation="12"
                               @click="togglePicker()"
                               v-model="insideValue">

                    <v-spacer></v-spacer>
                    <v-btn text color="primary" @click="togglePicker()">Cancel</v-btn>
                    <v-btn text color="primary" @click="update">OK</v-btn>
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
                this.$refs.dialog.save(this.insideValue)
                this.$emit('on-update', {key: this.name, value: this.insideValue})
            }
        }
    }
</script>