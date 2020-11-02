<template>
    <div class="item-edit-container">
        <div class="header">
            <h2 v-if="userId">User information</h2>
            <h2 v-else>Add new user</h2>
            <span @click="close" class="close-icon"></span>
        </div>

        <div class="form-container">
            <div class="fields-container">

                <Input label="Reference" :value="user.firstname" placeholder=""/>
            </div>
        </div>
    </div>
</template>

<script>

    import {mapState} from "vuex";
    import _ from "lodash";

    export default {
        name: 'UserEdit',
        data() {
            return {
                userId: undefined,
            }
        },
        computed: {
            ...mapState({
                user(state) {
                    let find = _.find(state.users.users, user => user.id === this.userId);
                    return find ? _.cloneDeep(find) : {}
                }
            }),
        },
        watch: {
            '$route': 'fetchItem'
        },
        methods: {
            close() {
                this.$router.back()
            },
            fetchItem() {
                this.userId = parseInt(this.$route.params.id)
                if (this.userId) {
                    this.$store.dispatch('users/getById', this.userId)
                }
            }
        },
        created() {
            this.fetchItem()
        }
    }
</script>