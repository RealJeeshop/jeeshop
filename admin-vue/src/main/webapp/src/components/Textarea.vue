<template>
    <div class="field-container">
        <label>
            {{label}}
            <textarea v-model="value" rows="4" :placeholder="placeholder" />
        </label>
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