<template>
    <div class="content">
        <h1>Catalogs</h1>
        <Table of="catalogs" :items="catalogs" />
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import Table from './Table'

    export default {
        name: 'Catalogs',
        components: {
            Table
        },
        computed: mapState({
            catalogs: state => state.catalogs.catalogs
        }),
/*        methods: mapActions('cart', [
            'addProductToCart'
        ]),*/
        created () {
            this.$store.dispatch('catalogs/getCatalogs')
        }
    }
</script>

