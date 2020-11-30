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
                <tr v-for="rel in relationships" :key="rel.id" class="not-clickable">
                    <td class="">{{rel.id}}</td>
                    <td class="half-width">{{rel.name}}</td>
                    <td class="full-width">{{rel.visible ? 'Yes' : 'No'}}</td>
                    <td class="clickable"><i class="fa fa-trash"/></td>
                </tr>
            </tbody>
        </v-simple-table>
    </div>
</template>

<script>
    export default {
        name: 'RelationshipsTable',
        props: {
            label: String,
            itemType: String,
            values: Array[Object]
        },
        data() {
            return {
                relationships: this.values ? this.values : []
            }
        },
        watch: {
            values: function () {
                this.relationships = this.values ? this.values : []
            }
        },
        methods: {
            addNewRelationship() {
                this.$emit('open-edition', {itemType: this.itemType, ids: this.relationships.map(v => v.id)})
            }
        }
    }

</script>

<style scoped>
    .half-width {
        width: 35%;
    }
</style>