<template>
    <div class="associated-data-container">
        <div class="expand-header">
            <label>Presentations per locale</label>
        </div>
        <v-simple-table class="presentation-table">
            <tbody>
            <tr class="clickable">
                <td class="full-width">en</td>
                <td><i class="fa fa-trash"/></td>
            </tr>
            <tr class="clickable">
                <td class="full-width">fr</td>
                <td><i class="fa fa-trash"/> </td>
            </tr>
            </tbody>
        </v-simple-table>
    </div>

</template>

<script>
    export default {
        name: 'PresentationTable',
    }

</script>

<style lang="scss" scoped>


    td.full-width {
        width: 100%;
    }

    .expand-header {
        display: flex;
        justify-content: space-between;
        i {
            opacity: 80%;
            color: #757575;
        }
    }

    .associated-data-container {
        margin-bottom: 1em;
    }

</style>