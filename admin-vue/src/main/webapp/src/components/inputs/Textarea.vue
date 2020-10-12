<template>
    <div class="field-container">

            <v-textarea v-model="value" :label="label" rows="3" />
    </div>
</template>

<script>
    export default {
        name: 'Textarea',
        props: {
            label: String,
            value: String,
            placeholder: String,

        }
    }
</script>