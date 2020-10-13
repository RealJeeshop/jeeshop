<template>
    <v-dialog v-model="open" persistent max-width="900">
        <v-card>
            <v-card-title class="headline">Add new presentation</v-card-title>
            <v-card-text>
                <div class="fields-container">
                    <Select label="Select a locale" :items="locales"/>
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
    import Input from "./inputs/Input";
    import Select from "./inputs/Select";
    import Textarea from "./inputs/Textarea";
    import FileInput from "./inputs/FileInput";
    export default {
        name: 'LocaleEdition',
        components: {FileInput, Textarea, Select, Input},
        data() {
            return {
                locales: ["English", "Français", "Chinese"]
            }
        },
        props: {
            open: Boolean
        },
        methods: {
            cancel() {
                this.$emit("on-cancel")
            },
            addLocale() {
                this.$emit("on-save")
            }
        }
    }
</script>

<style lang="scss" scoped>

</style>