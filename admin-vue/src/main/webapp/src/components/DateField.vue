<template>
    <div class="field-container">
        <label>
            {{label}}

            <v-dialog ref="dialog" :return-value.sync="date" v-model="show" max-width="500">
                <template v-slot:activator="{ on, attrs }">
                    <input @click="togglePicker()" :value="date" :placeholder="label" v-bind="attrs" v-on="on"/>
                </template>
                <v-date-picker v-if="show"
                               :full-width="true"
                               elevation="12"
                               @click="togglePicker()"
                               v-model="date">

                    <v-spacer></v-spacer>
                    <v-btn text color="primary" @click="togglePicker()">Cancel</v-btn>
                    <v-btn text color="primary" @click="$refs.dialog.save(date)">OK</v-btn>
                </v-date-picker>

            </v-dialog>
        </label>
    </div>
</template>

<script>

    export default {
        name: 'DateField',
        data: () =>{
          return {
              show: false,
              date: new Date().toISOString().substr(0, 10),
          }
        },
        methods: {
            togglePicker() {
                this.show = !this.show
            }
        },
        props: {
            label: String
        }
    }
</script>