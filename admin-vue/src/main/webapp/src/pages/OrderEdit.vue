<template>
    <div class="item-edit-container">
        <div class="header">
            <h2>Order details</h2>
            <span @click="close" class="close-icon"></span>
        </div>

        <div class="form-container">

        </div>
    </div>
</template>

<script>
    import './edit-form.scss'
    export default {
        name: 'OrderEdit',
        methods: {
            close() {
                this.$router.back()
            },
        }
    }
</script>

<style lang="scss" scoped>
   .order-edit-container {
       background-color: #efefef;
       flex: 2;
       border-left: 1px solid #e0e0e0;
       width: 100%;
   }
</style>