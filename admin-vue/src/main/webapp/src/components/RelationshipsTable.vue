<template>
    <div class="associated-data-container">
        <div class="expand-header">
            <label>{{label}}</label>
            <div class="clickable" @click="addNewRelationship()">Add new</div>
        </div>

        <v-simple-table v-if="relationships.length > 0">
            <thead>
            <tr>
                <th class="text-left">Id</th>
                <th class="text-left">Name</th>
                <th class="text-left">Visible</th>
            </tr>
            </thead>
            <tbody>
                <tr v-for="rel in relationships" :key="rel.id" class="clickable">
                    <td class="full-width">{{rel.id}}</td>
                    <td class="full-width">{{rel.name}}</td>
                    <td class="full-width">{{rel.visible ? 'Yes' : 'No'}}</td>
                    <td><i class="fa fa-trash"/></td>
                </tr>
            </tbody>
        </v-simple-table>

        <RelationshipsEdition :open="showEdition" :itemType="itemType" @on-cancel="showEdition = false" @on-save="save"/>
    </div>
</template>

<script>
    import RelationshipsEdition from "./RelationshipsEdition";
    export default {
        name: 'RelationshipsTable',
        components: {RelationshipsEdition},
        props: {
            label: String,
            values: Array[Object],
            itemType: String
        },
        data() {
            return {
                relationships: this.values ? this.values : [],
                showEdition: false
            }
        },
        methods: {
            addNewRelationship() {
                this.showEdition = true
            },
            save() {
                this.showEdition = false
            }
        }
    }

</script>

<style>
    .half-width {
        width: 50%;
    }
</style>