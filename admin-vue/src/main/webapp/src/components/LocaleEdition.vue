<template>
    <v-dialog v-model="open" persistent max-width="900">
        <v-card>
            <v-card-title v-if="presentation.locale">Edit presentation for locale {{presentation.locale}}</v-card-title>
            <v-card-title v-else class="headline">Add new presentation</v-card-title>
            <v-card-text>
                <div v-if="!presentation.locale" class="fields-container">
                    <Select label="Select a locale" :items="availableLocales"/>
                </div>
                <div class="fields-container">
                    <Input label="Name" :value="presentation.displayName" @on-update="update" />
                    <Input label="Promotion text" :value="presentation.promotion" @on-update="update"/>
                    <Textarea label="Short description" :value="presentation.shortDescription" @on-update="update" />
                    <Textarea label="Medium description" :value="presentation.mediumDescription" @on-update="update" />
                    <Textarea label="Long description" :value="presentation.longDescription" @on-update="update"/>
                </div>
                <div class="fields-container">
                    <FileInput label="Thumbnail" :value="presentation.thumbnail" @on-update="update" />
                    <FileInput label="Small Image" :value="presentation.smallImage" @on-update="update" />
                    <FileInput label="Large Image" :value="presentation.largeImage" @on-update="update" />
                    <FileInput label="Video" :value="presentation.video" @on-update="update" />
                </div>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn v-if="presentation" color="black darken-1" text @click="addLocale()">Add</v-btn>
                <v-btn v-else color="black darken-1" text @click="updateLocale()">Update</v-btn>
                <v-btn color="red darken-1" text @click="cancel()">Cancel</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import Input from "./inputs/Input";
    import Select from "./inputs/Select";
    import Textarea from "./inputs/Textarea";
    import FileInput from "./inputs/FileInput";
    export default {
        name: 'LocaleEdition',
        components: {FileInput, Textarea, Select, Input},
        data() {
            return {
                presentation: this.data ? this.data : {},
                availableLocales: ["English", "French", "Chinese"]
            }
        },
        props: {
            open: Boolean,
            data: Object
        },
        watch: {
          data() {
              this.presentation = this.data ? this.data : {}
          }
        },
        methods: {
            cancel() {
                this.$emit("on-cancel")
            },
            addLocale() {
                this.$emit("on-save")
            },
            updateLocale() {
                // TODO dispatch the update of the presentation
                this.$emit("on-save")
            },
            update(field) {
                this.presentation[field.key] = field.value
            },
        }
    }
</script>

<style lang="scss" scoped>

</style>