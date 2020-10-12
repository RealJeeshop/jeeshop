<template>
    <div class="associated-data-container">
        <label>{{label}}</label>
        <v-simple-table>
            <thead>
            <tr>
                <th class="text-left">Id</th>
                <th class="text-left">Name</th>
                <th class="text-left">Visible</th>
            </tr>
            </thead>
            <tbody>
            <tr class="clickable">
                <td>1</td>
                <td class="half-width">Child</td>
                <td class="half-width">Yes</td>
                <td><i class="fa fa-trash"/></td>
            </tr>
            <tr class="clickable">
                <td>2</td>
                <td class="half-width">Child 2</td>
                <td class="half-width">No</td>
                <td><i class="fa fa-trash"/> </td>
            </tr>
            </tbody>
        </v-simple-table>
    </div>
</template>

<script>
    export default {
        name: 'RelationshipsTable',
        props: {
            label: String
        }
    }

</script>

<style>
    .half-width {
        width: 50%;
    }
</style>