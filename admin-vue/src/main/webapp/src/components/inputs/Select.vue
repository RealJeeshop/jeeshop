<template>
    <div class="field-container">
        <v-select :items="items"
                  :label="label"
                  :placeholder="placeholder" />
    </div>
</template>

<script>
    export default {
        name: 'Select',
        props: {
            label: String,
            items: Array[String],
            placeholder: String
        }
    }
</script>