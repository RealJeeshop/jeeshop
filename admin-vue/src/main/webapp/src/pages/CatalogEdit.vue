<template>
    <div class="catalog-edit-container">
        <i @click="close()" class="fa fa-times" />
        <div>
            <span>Computed visibility: {{visible}}</span>
            <em>Only visible items are available in store</em>
        </div>
        <div>
            <label><input type="checkbox" /> Disabled</label>
            <em>Check to prevent this item to appear in store</em>
        </div>

        <div class="">
            <Input label="Name" :value="item.name" placeholder="name"/>
            <Textarea label="Description" :value="item.description" placeholder="description"  />
        </div>
    </div>
</template>

<script>

    import Input from "../components/Input";
    import Textarea from "../components/Textarea";
    export default {
        name: 'CatalogEdit',
        components: {Input, Textarea},
        data: () => {
            return {
                visible: 'visible'
            }
        },
        computed: {
            item () {
                return this.$store.getters['catalogs/getById'](this.$route.params.id)
            }
        },
        methods: {
          close: () => {
              this.$router.go(-1)
          }
        },
        created () {

            console.log("catalog edit creation")
            console.log('this.$route.params.id : ' + JSON.stringify(this.$route.params.id))

        },

    }
</script>