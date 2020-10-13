<template>
    <div class="field-container">
        <label>
            <VFileInput :label="label" />
        </label>
    </div>
</template>

<script>
    export default {
        name: 'FileInput',
        props: {
            label: String,
            value: String,
            placeholder: String,
            name: String,
            hint: String
        },
    }
</script>