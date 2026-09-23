import { useState } from 'react'

export function useSavedFeedback() {
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)

  const run = async (accion) => {
    setSaving(true)
    setSaved(false)
    try {
      if (accion) await accion()
      setSaved(true)
      window.setTimeout(() => setSaved(false), 2000)
    } finally {
      setSaving(false)
    }
  }

  return { saving, saved, run }
}
