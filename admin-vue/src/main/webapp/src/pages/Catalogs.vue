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

            <v-btn color="primary" dark @click="createItem()">New {{itemType.slice(0, -1)}}</v-btn>
        </v-toolbar>
        <div class="content">
            <Dialog :dialog="showWarningDialog"
                    title="Changes will not be saved"
                    message="You have pending changes. Are you sure you want to quit ?"
                    @agreed="handleChoice"/>
            <Table :of="itemType" :headers="headers" :items="items" @item-selected="handleItemSelection" />
            <router-view name="edit" />
        </div>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import Table from '../components/Table'
    import Dialog from "../components/Dialog";
    import _ from  'lodash'

    export default {
        name: 'Catalogs',
        components: {
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
            this.showEditPanel = !!this.$route.params.id;
            if (!this.showEditPanel) this.$store.dispatch('catalogs/getItems', this.itemType)
        },

        updated() {

            this.itemType = this.$route.params.itemType
            this.showEditPanel = !!this.$route.params.id;
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

