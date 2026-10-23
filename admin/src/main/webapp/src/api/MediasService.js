import axios from 'axios'

const MediasService = {

    getImage(type, id, locale, filename) {
        return new Promise((success, die) => {

            axios.get(`/rs/medias/${type}/${id}/${locale}/${filename}`).then(response => {
                console.log("data : " + JSON.stringify(response.data))
                success(response.data)
            }).catch(die)
        })
    },

    async uploadImage(type, id, locale, image) {
        console.log("uploading image")
        return new Promise((success, die) => {

            console.log("image.name : " + JSON.stringify(image))
            const formData = new FormData();
            formData.append('file', image)

            axios.post(`/rs/medias/${type}/${id}/${locale}/upload`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }).then(response => {
                console.log("data : " + JSON.stringify(response.data))
                success(response.data)
            }).catch(die)
        })
    },
    async uploadImages(type, id, locale, images) {
        console.log("uploading images")

        return new Promise((success, die) => {
            return Promise.all(images.map(image => {
                const formData = new FormData();
                formData.append('file', image.file)
                return axios.post(`/rs/medias/${type}/${id}/${locale}/upload`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                })
            })).then(values => {
                console.log("data : " + JSON.stringify(values))
                success(values.data)
            }).catch(die)
        })
    },

}

export default MediasService