import { useEffect, useRef } from 'react'
import type { ReactNode } from 'react'

interface Props {
  title: string
  onClose: () => void
  children: ReactNode
}

function Modal({ title, onClose, children }: Props) {
  const dialogRef = useRef<HTMLDialogElement>(null)

  useEffect(() => {
    dialogRef.current?.showModal()
  }, [])

  return (
    <dialog ref={dialogRef} onClose={onClose}>
      <h2>{title}</h2>
      {children}
    </dialog>
  )
}

export default Modal
