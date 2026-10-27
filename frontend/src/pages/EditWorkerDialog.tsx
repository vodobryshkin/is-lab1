import { useEffect, useRef } from 'react'
import { useUpdateWorkerMutation } from '../api/api'
import type { Worker } from '../api/api'
import { workerRequest, workerStateFrom } from '../forms'
import type { WorkerState } from '../forms'
import WorkerForm from './WorkerForm'

interface Props {
  worker: Worker
  onClose: () => void
}

function EditWorkerDialog({ worker, onClose }: Props) {
  const dialogRef = useRef<HTMLDialogElement>(null)
  const [updateWorker, { isLoading, error }] = useUpdateWorkerMutation()

  useEffect(() => {
    dialogRef.current?.showModal()
  }, [])

  async function handleSubmit(state: WorkerState) {
    const result = await updateWorker({ id: worker.id, body: workerRequest(state, true) })
    if (!result.error) {
      dialogRef.current?.close()
    }
  }

  return (
    <dialog ref={dialogRef} onClose={onClose}>
      <h2>Изменение работника с ID {worker.id}</h2>
      <WorkerForm
        initial={workerStateFrom(worker)}
        forUpdate={true}
        submitTitle="Сохранить"
        isLoading={isLoading}
        error={error}
        onSubmit={handleSubmit}
        onCancel={() => dialogRef.current?.close()}
      />
    </dialog>
  )
}

export default EditWorkerDialog
