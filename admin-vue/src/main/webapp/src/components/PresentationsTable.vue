<template>
    <div class="associated-data-container">
        <div class="expand-header">
            <label>Presentations per locale</label>
            <div class="clickable" @click="addNewLocale()">Add new</div>
        </div>
        <v-simple-table class="presentation-table">
            <tbody>
                <tr v-for="presentation in presentations" :key="presentation.id" class="clickable" @click="editLocale(presentation)">
                    <td class="full-width">{{presentation}}</td>
                    <td class="clickable"><i class="fa fa-trash"/></td>
                </tr>
            </tbody>
        </v-simple-table>

        <LocaleEdition v-if="showLocaleEdition" :open="showLocaleEdition" :data="localeData" @on-cancel="showLocaleEdition = false" @on-save="saveLocale"/>
    </div>

</template>

<script>
    import LocaleEdition from "./LocaleEdition";
    export default {
        name: 'PresentationTable',
        components: {LocaleEdition},
        props: {
          value: Object
        },
        data() {
            return {
                itemId: this.value ? this.value.itemId : undefined,
                itemType: this.value ? this.value.itemType : undefined,
                presentations: this.value ? this.value.availableLocales : [],
                showLocaleEdition: false,
                localeData: {}
            }
        },
        watch: {
            value() {
                this.presentations = this.value ? this.value.availableLocales : []
            }
        },
        methods: {
            addNewLocale() {
                this.showLocaleEdition = true
            },
            saveLocale() {
                console.log("saving locale")
                this.showLocaleEdition = true
            },
            editLocale(locale) {
                this.localeData = {
                    itemType: this.itemType,
                    itemId: this.itemId,
                    locale: locale
                }
                this.showLocaleEdition = true
            }
        },
        created() {
            console.log("creating presentation table")
        },
        updated() {
            console.log("updating presentation table")
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