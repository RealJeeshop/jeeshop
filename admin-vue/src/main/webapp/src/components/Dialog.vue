<template>
    <v-dialog v-model="dialog" persistent max-width="290">
        <v-card>
            <v-card-title class="headline">
               {{title}}
            </v-card-title>
            <v-card-text>{{message}}</v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="black darken-1" text @click="disagree()">
                    No
                </v-btn>
                <v-btn color="red darken-1" text @click="agree()">
                    Yes
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        name: 'Dialog',
        props: {
            title: String,
            message: String,
            dialog: Boolean
        },
        methods: {
            agree() {
                this.dialog = false
                this.$emit('agreed', true)
            },
            disagree() {
                this.dialog = false
                this.$emit('agreed', false)
            }
        }
    }
</script>