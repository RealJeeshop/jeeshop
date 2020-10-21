<template>
    <v-dialog v-model="open" persistent max-width="900">
        <v-card>
            <v-card-title class="headline">Select items</v-card-title>
            <v-card-text>
                <Table :of="itemType" :headers="headers" :items="items" @item-selected="onItemSelected">
                    <template v-slot:actions="{ item }">
                        <v-checkbox input-value="false" :value="selectedIds.indexOf(item.id) === -1" />
                    </template>
                </Table>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="black darken-1" text @click="addRelationship()">Add</v-btn>
                <v-btn color="red darken-1" text @click="cancel()">Cancel</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import Table from "./Table";
    import {mapState} from "vuex";
    import _ from "lodash";

    export default {
        name: 'RelationshipsEdition',
        components: {Table},
        data() {
            return {
                search: "",
                selectedIds: this.selectedRelationships ? this.selectedRelationships : [],
                headers: [
                    {text: "Name", value: "name"},
                    {text: "Description", value: "description"},
                    {text: "Start Date", value: "startDate"},
                    {text: "End Date", value: "endDate"},
                    {text: "Visible", value: "visible"},
                    {text: "Disabled", value: "disabled"},
                    {text: "", value: "actions", sortable: false}
                ]
            }
        },
        props: {
            open: Boolean,
            itemType: String,
            selectedRelationships: Array[Number]
        },
        watch: {
            selectedRelationships() {
                this.selectedIds = this.selectedRelationships.splice(0, 0)
            }
        },
        computed: {
            ...mapState({
                items(state) {
                    //FIXME duplicate with catalogs.vue
                    let path = this.itemType
                    let tmp = _.cloneDeep(state)
                    if (path === 'catalogs') {
                        return tmp.catalogs.catalogs
                    } else if (path === 'products') {
                        return tmp.catalogs.products
                    } else if (path === 'categories') {
                        return tmp.catalogs.categories
                    } else if (path === 'skus') {
                        return tmp.catalogs.skus
                    } else if (path === 'discounts') {
                        return tmp.catalogs.discounts
                    }
                },
            }),
        },
        methods: {
            cancel() {
                this.$emit("on-cancel")
            },
            addRelationship() {
                this.$emit("on-save")
            },
            onItemSelected(id) {
                console.log('id : ' + JSON.stringify(id))
            }
        }
    }
</script>

<style lang="scss" scoped>

</style>