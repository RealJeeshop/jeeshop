import { h } from 'vue'

const flexUtilities = [
  'flex-column',
  'flex-wrap',
  'align-center',
  'justify-space-between',
  'justify-space-around'
]

export default {
  inheritAttrs: false,
  setup(_, { attrs, slots }) {
    return () => {
      const { class: className, ...otherAttrs } = attrs
      const utilities = flexUtilities.filter(name => Object.hasOwn(otherAttrs, name))
      flexUtilities.forEach(name => delete otherAttrs[name])

      return h('div', {
        ...otherAttrs,
        class: ['d-flex', ...utilities, className]
      }, slots.default?.())
    }
  }
}
