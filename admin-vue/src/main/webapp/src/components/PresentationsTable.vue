<template>
    <div class="associated-data-container">
        <div class="expand-header">
            <label>Presentations per locale</label>
            <div class="clickable" @click="addNewLocale()">Add new</div>
        </div>
        <v-simple-table class="presentation-table">
            <tbody>
                <tr v-for="presentation in presentations" :key="presentation.id" class="clickable">
                    <td class="full-width">{{presentation.locale}}</td>
                    <td><i class="fa fa-trash"/></td>
                </tr>
            </tbody>
        </v-simple-table>

        <LocaleEdition :open="showLocaleEdition" @on-cancel="showLocaleEdition = false" @on-save="saveLocale"/>
    </div>

</template>

<script>
    import LocaleEdition from "./LocaleEdition";
    export default {
        name: 'PresentationTable',
        components: {LocaleEdition},
        props: {
          value: Array[Object]
        },
        data() {
            return {
                presentations: this.value ? this.value : [],
                showLocaleEdition: false
            }
        },
        methods: {
            addNewLocale() {
                this.showLocaleEdition = true
            },
            saveLocale() {
                console.log("saving locale")
                this.showLocaleEdition = true
            }
        }
    }

</script>

<style lang="scss">


    td.full-width {
        width: 100%;
    }

    .expand-header {
        display: flex;
        justify-content: space-between;
        i {
            opacity: 80%;
            color: #757575;
        }
    }

    .associated-data-container {
        margin-bottom: 1em;
    }

</style>