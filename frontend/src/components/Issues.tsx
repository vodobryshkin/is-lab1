import type { ValidationIssue } from '../api/errors'

interface Props {
  issues: ValidationIssue[]
  field: string
}

function Issues({ issues, field }: Props) {
  return issues
    .filter((issue) => issue.field === field)
    .map((issue) => (
      <small key={issue.code} role="alert">
        {issue.message}
      </small>
    ))
}

export default Issues
