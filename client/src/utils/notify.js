import { createApp, ref, watch } from 'vue'
import MobileAlert from '../components/Notify.vue'

export function showAlert(message, duration = 2000) {
  const container = document.createElement('div')
  document.body.appendChild(container)

  const visible = ref(true)

  const app = createApp(MobileAlert, {
    show: visible.value,
    message
  })

  app.mount(container)

  // 自动关闭
  if (duration > 0) {
    setTimeout(() => {
      visible.value = false
      app.unmount()
      container.remove()
    }, duration)
  }
}
