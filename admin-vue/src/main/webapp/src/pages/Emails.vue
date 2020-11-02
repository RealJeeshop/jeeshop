<template>
    <div class="page-content column">
        <v-toolbar color="#484848" dark flat>
            <v-tabs class="sub-header-menu" align-with-title>
                <v-tabs-slider color="yellow"></v-tabs-slider>
                <v-tab to="/emails">Templates</v-tab>
                <v-tab to="/emails/operations">Operations</v-tab>
            </v-tabs>
            <v-btn color="primary" dark @click="createMailTemplate">New mail template</v-btn>
        </v-toolbar>
        <div class="content">
            <MailTemplateTable :headers="headers" :items="mails"
                       @item-selected="handleItemSelection" />

            <router-view />
        </div>
    </div>
</template>

<script>
    import MailTemplateTable from "../components/MailTemplateTable";
    import {mapState} from "vuex";
    import _ from "lodash";

    export default {
        name: 'Emails',
        components: {MailTemplateTable},
        data() {
            return {
                showEditPanel: false,
            }
        },
        computed: {
            ...mapState({
                mails(state) {
                    return _.cloneDeep(state.mails.mails);
                }
            }),
            headers() {
                let headers = [
                    {text: "Id", value: "id"},
                    {text: "name", value: "name"},
                    {text: "Locale", value: "locale"},
                    {text: "Creation Date", value: "creationDate"},
                    {text: "Last Modification", value: "updateDate"},
                ]

                if (this.showEditPanel) {
                    return headers.splice(0, 2)

                } else return headers
            }
        },
        methods: {
            createMailTemplate() {
                this.$router.push('/emails/create')
            },
            handleItemSelection(id) {
                console.log('selected mail template id : ' + JSON.stringify(id))
                this.$router.push(`/emails/${id}`)
            }
        },
        created() {
            this.showEditPanel = !!this.$route.params.id;
            if (!this.showEditPanel) this.$store.dispatch('mails/getAll')
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