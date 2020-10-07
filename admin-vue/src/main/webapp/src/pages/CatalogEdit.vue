<template>
    <div class="catalog-edit-container">
        <i @click="close()" class="fa fa-2x fa-times close-icon" />
        <div class="header">
            <span>visibility: {{visible}}</span>
            <em>Only visible items are available in store</em>
            <v-checkbox v-model="visibility" label="Disabled"></v-checkbox>
            <em>Check to prevent this item to appear in store</em>
        </div>

        <div class="form-container">
            <Input label="Name" :value="item.name" placeholder="name"/>
            <Textarea label="Description" :value="item.description" placeholder="description"  />
            <DateField label="Start Date" />
            <DateField label="End Date" />
            <div class="field-container">
                <label>
                    <span>Texts and medias</span>
                    <v-expansion-panels>
                        <v-expansion-panel>
                            <v-expansion-panel-header>
                                Presentations per locale
                            </v-expansion-panel-header>
                            <v-expansion-panel-content>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </label>
            </div>
            <div class="field-container">
                <label>
                    <span>Relationships</span>
                    <v-expansion-panels>
                        <v-expansion-panel>
                            <v-expansion-panel-header>
                                Root categories
                            </v-expansion-panel-header>
                            <v-expansion-panel-content>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </label>
            </div>


        </div>
    </div>
</template>

<script>

    import Input from "../components/Input";
    import Textarea from "../components/Textarea";
    import DateField from "../components/DateField";

    export default {
        name: 'CatalogEdit',
        components: {Input, Textarea, DateField},
        data: () => {
            return {
                visible: 'visible',
                date: new Date()
            }
        },
        computed: {
            item () {
                return this.$store.getters['catalogs/getById'](this.$route.params.id)
            }
        },
        methods: {
          close() {
              console.log("AHHHHHH")
              this.$router.go(-1)
          }
        },
        created () {

            console.log("catalog edit creation")
            console.log('this.$route.params.id : ' + JSON.stringify(this.$route.params.id))

        },

    }
</script>

<style lang="scss" scoped>

    .catalog-edit-container div:nth-child(1){


            display: flex;
            flex-direction: column;
    }

    .close-icon {
        position: absolute;
        right: 0;
        padding: 10px;
        cursor: pointer;
    }

    .header {
        display: flex;
        flex-direction: column;
    }

    .form-container {
        display: flex;
        flex-wrap: wrap;
    }

    .field-container {
        min-width: 40%;
        margin-right: 1em;

    }

</style>