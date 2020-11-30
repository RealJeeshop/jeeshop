import MailsAPI from "../../api/MailsAPI";
import _ from "lodash";

const state = () => ({
    mails: []
})

const getters = {

}

const actions = {

    getAll({commit}) {
        MailsAPI.getAll()
            .then(response => {
                commit('setMailTemplates', response)
            }).catch(error => {
            console.log('error fetching mail templates: ' + JSON.stringify(error))
        })
    },
    getById({commit}, id) {
        MailsAPI.getById(id)
            .then(response => {
                commit('addMailTemplate', response)
            }).catch(error => {
            console.log(`error fetching mail template with id ${id} due to ${JSON.stringify(error)}`)
        })
    }
}

const mutations = {

    setMailTemplates(state, mails) {
        state.mails = mails
    },

    addMailTemplate(state, mail) {

        let existingId = state.mails.findIndex(u => u.id === mail.id)
        let clonedState = _.cloneDeep(state.mails)
        if (existingId === -1) {
            clonedState.push(mail)
        } else {
            clonedState.splice(existingId, 1, mail)
        }

        state.mails = clonedState
    }
}

export default {
    namespaced: true,
    state,
    actions,
    getters,
    mutations
}