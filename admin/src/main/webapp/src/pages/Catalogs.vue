<template>
    <div class="page-content column">
        <v-toolbar color="#484848" dark flat>
            <v-tabs class="sub-header-menu" align-with-title>
                <v-tabs-slider color="yellow"></v-tabs-slider>
                <v-tab to="/stores">Stores</v-tab>
                <v-tab to="/catalogs">Catalogs</v-tab>
                <v-tab to="/categories">Categories</v-tab>
                <v-tab to="/products">Products</v-tab>
                <v-tab to="/skus">SKUs</v-tab>
                <v-tab to="/discounts">Discounts</v-tab>
            </v-tabs>

            <v-btn color="primary" dark @click="createItem()">New {{itemType.slice(0, -1)}}</v-btn>
        </v-toolbar>
        <div class="content">
            <Dialog :dialog="showWarningDialog"
                    title="Changes will not be saved"
                    message="You have pending changes. Are you sure you want to quit ?"
                    @agreed="handleChoice"/>
            <CatalogItemTable :of="itemType" :headers="headers" :items="items" @item-selected="handleItemSelection" />
            <router-view name="edit" />
        </div>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import CatalogItemTable from '../components/CatalogItemTable'
    import Dialog from "../components/Dialog";
    import _ from  'lodash'

    export default {
        name: 'Catalogs',
        components: {
            CatalogItemTable,
            Dialog
        },
        data: () => {
            return {
                itemType: 'catalogs',
                showEditPanel: false,
                showWarningDialog: false,
                itemId: null
            }
        },
        computed: {
            ...mapState({
                items(state) {
                    let path = this.itemType
                    let tmp = _.cloneDeep(state)
                    if (path === 'stores') {
                        return tmp.catalogs.stores
                    } else if (path === 'catalogs') {
                        return tmp.catalogs.catalogs
                    } else if (path === 'products') {
                        return tmp.catalogs.products
                    } else if(path === 'categories') {
                        return tmp.catalogs.categories
                    } else if (path === 'skus') {
                        return tmp.catalogs.skus
                    } else if (path === 'discounts') {
                        return tmp.catalogs.discounts
                    }
                },
                isAdmin(state) {
                  return state.session.user
                      ? state.session.user.roles.filter(r => r.name === "admin").length === 1
                      : false
                },
                item(state) {
                    return _.find(state.catalogs[this.itemType], item => item.id === this.itemId);
                }
            }),

            headers() {
                let headers = [
                    {text: "Name", value: "name"},
                    {text: "Description", value: "description"},
                    {text: "Start Date", value: "startDate"},
                    {text: "End Date", value: "endDate"},
                    {text: "Visible", value: "visible"},
                    {text: "Disabled", value: "disabled"}
                ]

                if (this.showEditPanel) {
                    return headers.splice(0, 2)

                } else return headers
            }
        },
        methods: {
            handleItemSelection(id) {

                if (this.showEditPanel) {

                    this.showWarningDialog = true
                    this.nextItemid = id
                } else {

                    this.showEditPanel = true
                    this.$router.push(`/${this.itemType}/${id}`)
                }
            },
            handleChoice(agreed) {

                if (agreed) {
                    this.showWarningDialog = false
                    this.$router.replace(`/${this.itemType}/${this.nextItemid}`)
                } else {
                    this.showWarningDialog = false
                }
            },
            createItem() {
                this.$router.push(`/${this.itemType}/create`)
            }

        },
        created () {
            this.itemType = this.$route.params.itemType
            this.showEditPanel = !!this.$route.params.id || this.$route.path.indexOf("create") !== -1;
            if (!this.showEditPanel) {
              if (this.isAdmin) this.$store.dispatch('catalogs/getItems', this.itemType)
              else this.$store.dispatch('catalogs/getManagedItems', this.itemType)
            }
        },

        updated() {
            this.itemType = this.$route.params.itemType
            this.showEditPanel = !!this.$route.params.id || this.$route.path.indexOf("create") !== -1;
        }
    }
</script>

<style lang="scss" scoped>

    .table-container {
        flex: 1;
    }

</style>

