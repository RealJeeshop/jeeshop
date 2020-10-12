<template>
    <div class="field-container">
        <label>
            <v-dialog ref="dialog" :return-value.sync="date" v-model="show" max-width="500">
                <template v-slot:activator="{ on, attrs }">
                    <v-text-field
                            @click="togglePicker()"
                            :label="label"
                            :value="date"
                            :placeholder="label"
                            v-bind="attrs" v-on="on"/>
                </template>
                <v-date-picker v-if="show"
                               :full-width="true"
                               elevation="12"
                               @click="togglePicker()"
                               v-model="date">

                    <v-spacer></v-spacer>
                    <v-btn text color="primary" @click="togglePicker()">Cancel</v-btn>
                    <v-btn text color="primary" @click="update">OK</v-btn>
                </v-date-picker>

            </v-dialog>
        </label>
    </div>
</template>

<script>

    export default {
        name: 'DateField',
        data() {
          return {
              show: false,
              date: new Date().toISOString().substr(0, 10),
              insideValue: this.value
          }
        },
        props: {
            label: String,
            name: String,
            value: Number
        },
        methods: {
            togglePicker() {
                this.show = !this.show
            },
            update() {
                this.$refs.dialog.save(this.date)
                this.$emit('on-update', {key: this.name, value: this.date})
            }
        }
    }
</script>