<template>
    <div class="content">
        <h1>Catalogs</h1>
        <div class="catalog-items-menu">
            <router-link to="/catalogs">Catalogs</router-link>
            <router-link to="/categories">Categories</router-link>
            <router-link to="/products">Products</router-link>
            <router-link to="/skus">SKUs</router-link>
            <router-link to="/discounts">Discounts</router-link>
        </div>

        <Table :of="itemType" :items="items" @item-selected="handleItemSelection" />
        <v-navigation-drawer v-if="itemId" absolute right width="60%" >
            <CatalogEdit :item-type="itemType" :item-id="itemId" />
        </v-navigation-drawer>
        <router-view label="catalog-item-route"></router-view>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import Table from './Table'
    import CatalogEdit from "../pages/CatalogEdit";

    export default {
        name: 'Catalogs',
        components: {
            CatalogEdit,
            Table
        },
        data: () => {
            return {
                itemType: 'catalogs',
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
                this.$router.push(`${this.itemType}/${id}`)
            }
        },
        created () {

            console.log("CREATED")

            let match = new RegExp(/\/(.*)\/([0-9])*/).exec(this.$route.path)
                || new RegExp(/\/(.*)/).exec(this.$route.path)

            if (match) {
                this.itemType = match[1]
                this.itemId = match[2]
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
                this.itemId = match[2]
                this.$store.dispatch('catalogs/setItemType', this.itemType)
            }
        }
    }
</script>

<style lang="scss" scoped>

    .catalog-items-menu {

        display: flex;

        a {
            display: block;
            padding: 10px 15px;
            text-decoration: none;
            color: #337ab7;
            background-color: #fff;
            border-radius: 4px;
            margin-right: 1em;
        }

        a.router-link-exact-active {
            color: #fff;
            background-color: #337ab7;
        }
    }
</style>

