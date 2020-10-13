<template>
    <div class="page-content column">
        <v-toolbar color="#484848" dark flat>
            <v-tabs class="catalog-items-menu" align-with-title>
                <v-tabs-slider color="yellow"></v-tabs-slider>

                <v-tab to="/catalogs">Catalogs</v-tab>
                <v-tab to="/categories">Categories</v-tab>
                <v-tab to="/products">Products</v-tab>
                <v-tab to="/skus">SKUs</v-tab>
                <v-tab to="/discounts">Discounts</v-tab>
            </v-tabs>

            <v-btn color="primary" dark @click="createItem()">New Item</v-btn>
        </v-toolbar>
        <div class="content">
            <Dialog :dialog="showWarningDialog"
                    title="Changes will not be saved"
                    message="You have pending changes. Are you sure you want to quit ?"
                    @agreed="handleChoice"/>

            <Table :of="itemType" :headers="headers" :items="items" @item-selected="handleItemSelection" />
            <CatalogEdit v-if="showEditPanel" :item-type="itemType" :item-id="itemId" @on-close="handleEditPanelClose"/>
        </div>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import Table from '../components/Table'
    import CatalogEdit from "../components/CatalogEdit";
    import Dialog from "../components/Dialog";
    import _ from  'lodash'

    export default {
        name: 'Catalogs',
        components: {
            CatalogEdit,
            Table,
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
                    if (path === 'catalogs') {
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
                    console.log("showing warning dialog")
                    this.showWarningDialog = true
                    this.nextItemid = id
                } else {
                    this.$router.push(`/${this.itemType}/${id}`)
                }
            },
            handleEditPanelClose() {
                this.showEditPanel = false
                this.$router.back()
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

            console.log("CREATING CATALOGS COMPONENT")

            let match = new RegExp(/\/(.*)\/([0-9]|create)*/).exec(this.$route.path)
                || new RegExp(/\/(.*)/).exec(this.$route.path)

            if (match) {
                this.itemType = match[1]
                this.itemId = match[2] ? match[2] === 'create' ? undefined : parseInt(match[2]) : undefined
                this.showEditPanel = match[2] !== undefined
                this.$store.dispatch('catalogs/getItems', this.itemType)
            }
        },

        updated() {

            console.log("UPDATING CATALOGS COMPONENT")

            let match = new RegExp(/\/(.*)\/([0-9])*/).exec(this.$route.path)
                || new RegExp(/\/(.*)/).exec(this.$route.path)
            if (match) {
                this.itemType = match[1]
                this.itemId = match[2] ? parseInt(match[2]) : undefined
            }
        }
    }
</script>

<style lang="scss" scoped>

    .table-container {
        flex: 1;
    }

    .catalog-items-menu {

        display: flex;

        a {
            text-decoration: none;
            color: #fff;

            height: 100%;
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        a.router-link-exact-active {
            color: #fff;
        }
    }
</style>

