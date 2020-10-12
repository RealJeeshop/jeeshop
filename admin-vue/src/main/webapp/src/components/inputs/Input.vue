<template>
    <div class="field-container">
        <label>
            <v-text-field
                    :label="label"
                    v-model="value"
                    :placeholder="placeholder"
                    :hint="hint"/>
        </label>
    </div>
</template>

<script>
    export default {
        name: 'Input',
        props: {
            label: String,
            value: String,
            placeholder: String,
            hint: String
        }
    }
</script>