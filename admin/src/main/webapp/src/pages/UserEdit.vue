<template>
    <div class="item-edit-container">
        <div class="header">
            <h2 v-if="userId">User information</h2>
            <h2 v-else>Add new user</h2>
            <span @click="close" class="close-icon"></span>
        </div>

        <div class="form-container">
            <div class="fields-container">

                <v-checkbox label="Activated" :value="user.activated" @on-update="update" />
                <v-checkbox label="Disabled" :value="user.disabled" @on-update="update" />
                <v-checkbox label="Newsletter" :value="user.newslettersSubscribed" @on-update="update" />
            </div>
            <div class="fields-container">
                <Input label="Email" :value="user.login" placeholder="" @on-update="update" />
                <Input label="Password" :value="user.password" placeholder="" @on-update="update" readonly disabled />
                <Input label="First Name" :value="user.firstname" placeholder="First name" @on-update="update" />
                <Input label="Last Name" :value="user.lastname" placeholder="Last name" @on-update="update" />
                <Input label="Gender" :value="user.gender" placeholder="Gender" @on-update="update" />
                <Input label="Phone number" :value="user.phoneNumber" placeholder="Phone number" @on-update="update" />
                <DateField class="half-width" label="Birth Date" :value="user.birthDate" @on-update="update" />
            </div>

            <div class="fields-container">
                <Input label="Last Modification Date" :value="formatDate(user.updateDate)" readonly disabled />
                <Input label="Creation Date" :value="formatDate(user.creationDate)" readonly disabled />
            </div>

            <v-btn color="primary" elevation="2" @click="saveUser">Save</v-btn>
        </div>
    </div>
</template>

<script>

    import {mapState} from "vuex";
    import Input from "../components/inputs/Input";
    import _ from "lodash";
    import DateField from "../components/inputs/DateField";
    import DateUtils from '../lib/dateUtils'

    export default {
        name: 'UserEdit',
        components: {DateField, Input},
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
            },
            saveUser() {
                this.close()
            },
            update(field) {
                this.user[field.key] = field.value
            },
            formatDate(date) {
                return DateUtils.formatDate(date)
            }
        },
        created() {
            this.fetchItem()
        }
    }
</script>