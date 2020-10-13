<template>
    <v-dialog v-model="open" persistent max-width="900">
        <v-card>
            <v-card-title class="headline">Add new presentation</v-card-title>
            <v-card-text class="form-container">
                <div class="field-containers">
                    <Select label="Select a locale" :items="locales"/>
                </div>
                <div class="field-containers">
                    <Input label="Name" />
                    <Input label="Promotion text" />
                    <Textarea label="Short description" />
                    <Textarea label="Medium description" />
                    <Textarea label="Long description" />
                </div>
                <div class="field-containers">
                    <FileInput label="Thumbnail" />
                    <FileInput label="Small Image" />
                    <FileInput label="Large Image" />
                    <FileInput label="Video" />
                </div>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="black darken-1" text @click="addLocale()">
                    Add
                </v-btn>
                <v-btn color="red darken-1" text @click="cancel()">
                    Can
                </v-btn>
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
    .form-container {
        display: flex;
        flex-direction: column;
    }

    .field-containers {
        display: flex;
        flex-wrap: wrap;
    }

    .field-container {
        margin-bottom: 1rem;
        flex: 1;
        min-width: 40%;
        margin-right: 1em;

    }
</style>