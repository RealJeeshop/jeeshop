<template>
    <v-dialog v-model="open" persistent max-width="900">
        <v-card>
            <v-card-title class="headline">Add new presentation</v-card-title>
            <v-card-text>
                <div class="fields-container">
                    <Select label="Select a locale" :items="availableLocales"/>
                </div>
                <div class="fields-container">
                    <Input label="Name" />
                    <Input label="Promotion text" />
                    <Textarea label="Short description" />
                    <Textarea label="Medium description" />
                    <Textarea label="Long description" />
                </div>
                <div class="fields-container">
                    <FileInput label="Thumbnail" />
                    <FileInput label="Small Image" />
                    <FileInput label="Large Image" />
                    <FileInput label="Video" />
                </div>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="black darken-1" text @click="addLocale()">Add</v-btn>
                <v-btn color="red darken-1" text @click="cancel()">Cancel</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import { mapState } from 'vuex'
    import Input from "./inputs/Input";
    import Select from "./inputs/Select";
    import Textarea from "./inputs/Textarea";
    import FileInput from "./inputs/FileInput";
    import _ from "lodash";
    export default {
        name: 'LocaleEdition',
        components: {FileInput, Textarea, Select, Input},
        data() {
            return {
                itemType: this.data.itemType,
                itemId: this.data.itemId,
                locale: this.data.locale,
                availableLocales: ["English", "French", "Chinese"]
            }
        },
        props: {
            open: Boolean,
            data: Object
        },
        computed: mapState({
           presentation(state) {
               // FIXME beurk
               let find = _.find(state.catalogs[this.itemType], item => item.id === this.itemId);
               return find.availableLocales
                   ? find.availableLocales[this.locale]
                       ? find.availableLocales[this.locale]
                       : {}
                   : {};
           }
        }),
        methods: {
            cancel() {
                this.$emit("on-cancel")
            },
            addLocale() {
                this.$emit("on-save")
            }
        },
        created() {

            console.log('creating locale edition')
            this.$store.dispatch('catalogs/getPresentation', {
                itemType: this.itemType,
                itemId: this.itemId,
                locale: this.locale
            })
        },
        updated() {
            console.log('updating locale edition')
        }
    }
</script>

<style lang="scss" scoped>

</style>