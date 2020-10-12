<template>
    <div class="content">
        <v-toolbar color="#484848" dark flat>
            <v-tabs class="catalog-items-menu" align-with-title>
                <v-tabs-slider color="yellow"></v-tabs-slider>

                <v-tab to="catalogs">Catalogs</v-tab>
                <v-tab to="categories">Categories</v-tab>
                <v-tab to="products">Products</v-tab>
                <v-tab to="skus">SKUs</v-tab>
                <v-tab to="discounts">Discounts</v-tab>
            </v-tabs>

            <v-btn color="primary" dark>New Item</v-btn>
        </v-toolbar>

        <div>
            <Dialog :dialog="showWarningDialog"
                    title="Changes will not be saved"
                    message="You have pending changes. Are you sure you want to quit ?"
                    @agreed="handleChoice"/>

            <Table :of="itemType" :items="items" @item-selected="handleItemSelection" />
            <v-navigation-drawer v-if="showEditPanel" absolute right width="60%" >
                <CatalogEdit :item-type="itemType" :item-id="itemId" @on-close="handleEditPanelClose"/>
            </v-navigation-drawer>
        </div>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import Table from './Table'
    import CatalogEdit from "../pages/CatalogEdit";
    import Dialog from "./Dialog";

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
        computed: mapState({
            items: state => {
                let path = state.catalogs.itemType
                if (path === 'catalogs') {
                    return state.catalogs.catalogs
                } else if (path === 'products') {
                    return state.catalogs.products
                } else if(path === 'categories') {
                    return state.catalogs.categories
                } else if (path === 'skus') {
                    return state.catalogs.skus
                } else if (path === 'discounts') {
                    return state.catalogs.discounts
                }
            }
        }),
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
            }
        },
        created () {

            console.log("CREATED")

            let match = new RegExp(/\/(.*)\/([0-9])*/).exec(this.$route.path)
                || new RegExp(/\/(.*)/).exec(this.$route.path)

            if (match) {
                this.itemType = match[1]
                this.itemId = match[2] ? parseInt(match[2]) : undefined
                this.showEditPanel = match[2] !== undefined
                this.$store.dispatch('catalogs/setItemType', this.itemType)
                this.$store.dispatch('catalogs/getItems', this.itemType)
            }
        },

        updated() {

            console.log("UPDATED")

            let match = new RegExp(/\/(.*)\/([0-9])*/).exec(this.$route.path)
                || new RegExp(/\/(.*)/).exec(this.$route.path)

            if (match) {
                this.itemType = match[1]
                this.itemId = match[2] ? parseInt(match[2]) : undefined
                this.$store.dispatch('catalogs/setItemType', this.itemType)
            }
        }
    }
</script>

<style lang="scss" scoped>

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

