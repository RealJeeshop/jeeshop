<template>
    <div class="item-edit-container">
        <div class="header">
            <h2 v-if="userId">Mail template information</h2>
            <h2 v-else>Add new mail template</h2>
            <span @click="close" class="close-icon"></span>
        </div>

        <div class="form-container">
            <div class="fields-container">
                <Input label="Email" :value="mail.name" placeholder="" @on-update="update" />

            </div>

            <v-btn color="primary" elevation="2" @click="saveMailTemplate">Save</v-btn>
        </div>
    </div>
</template>

<script>

    import {mapState} from "vuex";
    import Input from "../components/inputs/Input";
    import _ from "lodash";
    import DateUtils from '../lib/dateUtils'

    export default {
        name: 'EmailEdit',
        components: {
            Input},
        data() {
            return {
                mailId: undefined,
            }
        },
        computed: {
            ...mapState({
                mail(state) {
                    let find = _.find(state.mails.mails, mail => mail.id === this.mailId);
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
                this.mailId = parseInt(this.$route.params.id)
                if (this.mailId) {
                    this.$store.dispatch('mails/getById', this.mailId)
                }
            },
            saveMailTemplate() {
                this.close()
            },
            update(field) {
                this.mail[field.key] = field.value
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