<template>
    <div class="page-content column">
        <v-toolbar color="#484848" dark flat>
            <v-btn color="primary" dark @click="createUser">New user</v-btn>
        </v-toolbar>
        <div class="content">
            <UserTable :headers="headers" :items="users"
                        @item-selected="handleItemSelection" />

            <router-view />
        </div>
    </div>
</template>

<script>
    import UserTable from "../components/UserTable";
    import {mapState} from "vuex";
    import _ from "lodash";

    export default {
        name: 'Users',
        components: {UserTable},
        data() {
            return {
                showEditPanel: false,
            }
        },
        computed: {
            ...mapState({
                users(state) {
                    return _.cloneDeep(state.users.users);
                }
            }),
            headers() {
                let headers = [
                    {text: "Id", value: "id"},
                    {text: "Login", value: "login"},
                    {text: "First Name", value: "firstname"},
                    {text: "Last Name", value: "lastname"},
                    {text: "Activated", value: "activated"},
                    {text: "Disabled", value: "disabled"},
                    {text: "Newsletters", value: "newslettersSubscribed"},
                    {text: "Creation Date", value: "creationDate"},
                    {text: "Last Modification", value: "updateDate"},
                ]

                if (this.showEditPanel) {
                    return headers.splice(0, 2)

                } else return headers
            }
        },
        methods: {
            createUser() {
                this.$router.push('/users/create')
            },
            handleItemSelection(id) {
                console.log('selected user id : ' + JSON.stringify(id))
                this.$router.push(`/users/${id}`)
            }
        },
        created () {
            this.showEditPanel = !!this.$route.params.id;
            if (!this.showEditPanel) this.$store.dispatch('users/getAll')
        },
        updated() {
            this.showEditPanel = !!this.$route.params.id;
        }
    }
</script>

<style lang="scss" scoped>
    .table-container {
        flex: 1;
    }
</style>