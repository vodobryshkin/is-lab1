import { useState } from 'react'
import { useCreateWorkerMutation } from '../api/api'
import { emptyWorker, workerRequest } from '../forms'
import type { WorkerState } from '../forms'
import WorkerDialog from './WorkerDialog'
import WorkerForm from './WorkerForm'

function CreateWorkerPage() {
  const [createWorker, { isLoading, error }] = useCreateWorkerMutation()
  const [createdId, setCreatedId] = useState<number | null>(null)
  const [formKey, setFormKey] = useState(0)
  const [openedId, setOpenedId] = useState<number | null>(null)

  async function handleSubmit(state: WorkerState) {
    const result = await createWorker(workerRequest(state, false))
    if (result.data) {
      setCreatedId(result.data.id)
      setFormKey(formKey + 1)
    }
  }

  return (
    <>
      <h2>Новый работник</h2>
      {createdId !== null && (
        <p>
          Работник создан, ID {createdId}.{' '}
          <button type="button" onClick={() => setOpenedId(createdId)}>
            Открыть
          </button>
        </p>
      )}
      <WorkerForm
        key={formKey}
        initial={emptyWorker}
        forUpdate={false}
        submitTitle="Создать"
        isLoading={isLoading}
        error={error}
        onSubmit={handleSubmit}
      />
      {openedId !== null && <WorkerDialog id={openedId} onClose={() => setOpenedId(null)} />}
    </>
  )
}

export default CreateWorkerPage
